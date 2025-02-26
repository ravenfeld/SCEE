package de.westnordost.streetcomplete.quests

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.AnyThread
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.size
import androidx.core.widget.NestedScrollView
import androidx.viewbinding.ViewBinding
import de.westnordost.countryboundaries.CountryBoundaries
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.meta.CountryInfo
import de.westnordost.streetcomplete.data.meta.CountryInfos
import de.westnordost.streetcomplete.data.meta.getByLocation
import de.westnordost.streetcomplete.data.osm.geometry.ElementGeometry
import de.westnordost.streetcomplete.data.quest.QuestKey
import de.westnordost.streetcomplete.data.quest.QuestType
import de.westnordost.streetcomplete.data.quest.QuestTypeRegistry
import de.westnordost.streetcomplete.databinding.ButtonPanelButtonBinding
import de.westnordost.streetcomplete.databinding.FragmentQuestAnswerBinding
import de.westnordost.streetcomplete.screens.main.bottom_sheet.AbstractBottomSheetFragment
import de.westnordost.streetcomplete.screens.main.bottom_sheet.IsMapOrientationAware
import de.westnordost.streetcomplete.util.FragmentViewBindingPropertyDelegate
import de.westnordost.streetcomplete.util.ktx.popIn
import de.westnordost.streetcomplete.util.ktx.popOut
import de.westnordost.streetcomplete.util.ktx.toast
import de.westnordost.streetcomplete.view.CharSequenceText
import de.westnordost.streetcomplete.view.ResText
import de.westnordost.streetcomplete.view.Text
import de.westnordost.streetcomplete.view.setText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

/** abstract base class for the form that is shown to answer a quest. I.e., it is...
 *  - a bottom sheet that can be pulled up to fill the screen (see AbstractBottomSheetFragment)
 *  - displays the quest title, has (an optional) content area, the floating OK button and a
 *    button bar
 *  - and more...
 */
abstract class AbstractQuestForm :
    AbstractBottomSheetFragment(), IsShowingQuestDetails, IsMapOrientationAware {

    // dependencies
    private val countryInfos: CountryInfos by inject()
    private val countryBoundaries: Lazy<CountryBoundaries> by inject(named("CountryBoundariesLazy"))
    private val questTypeRegistry: QuestTypeRegistry by inject()

    private var _binding: FragmentQuestAnswerBinding? = null
    private val binding get() = _binding!!

    override val bottomSheetContainer get() = binding.bottomSheetContainer
    override val bottomSheet get() = binding.bottomSheet
    override val scrollViewChild get() = binding.scrollViewChild
    override val bottomSheetTitle get() = binding.speechBubbleTitleContainer
    override val bottomSheetContent get() = binding.speechbubbleContentContainer
    override val floatingBottomView get() = binding.okButtonContainer
    override val floatingBottomView2 get() = binding.hideButton
    protected val scrollView: NestedScrollView get() = binding.scrollView
    override val hideButtonBottomMarginDp get() = if (binding.buttonPanel.size > 3) 32 else 8

    private var startedOnce = false

    private var _countryInfo: CountryInfo? = null // lazy but resettable because based on lateinit var
        get() {
            if (field == null) {
                field = countryInfos.getByLocation(
                    countryBoundaries.value,
                    geometry.center.longitude,
                    geometry.center.latitude,
                )
            }
            return field
        }
    protected val countryInfo get() = _countryInfo!!

    /** either DE or US-NY (or null), depending on what countryBoundaries returns */
    protected val countryOrSubdivisionCode: String? get() {
        val latLon = geometry.center
        return countryBoundaries.value.getIds(latLon.longitude, latLon.latitude).firstOrNull()
    }

    // passed in parameters
    override lateinit var questKey: QuestKey
    protected lateinit var questType: QuestType
    protected lateinit var geometry: ElementGeometry private set
    private var initialMapRotation = 0f
    private var initialMapTilt = 0f

    private var infoIsExpanded: Boolean = false

    // overridable by child classes
    open val contentLayoutResId: Int? = null
    open val contentPadding = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = requireArguments()
        questKey = Json.decodeFromString(args.getString(ARG_QUEST_KEY)!!)
        questType = questTypeRegistry.getByName(args.getString(ARG_QUESTTYPE)!!)!!
        geometry = Json.decodeFromString(args.getString(ARG_GEOMETRY)!!)
        initialMapRotation = args.getFloat(ARG_MAP_ROTATION)
        initialMapTilt = args.getFloat(ARG_MAP_TILT)
        _countryInfo = null // reset lazy field
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentQuestAnswerBinding.inflate(inflater, container, false)
        contentLayoutResId?.let { setContentView(it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTitle(resources.getString(questType.title))
        setTitleHintLabel(null)
        setHint(questType.hint?.let { resources.getString(it) })
        setHintImages(questType.hintImages.mapNotNull { requireContext().getDrawable(it) })

        binding.okButton.setOnClickListener {
            if (!isFormComplete()) {
                activity?.toast(R.string.no_changes)
            } else {
                onClickOk()
            }
        }

        infoIsExpanded = false
        binding.infoButton.setOnClickListener { toggleInfoArea() }
        binding.infoArea.setOnClickListener { toggleInfoArea() }

        // no content? -> hide the content container
        if (binding.content.childCount == 0) {
            binding.content.visibility = View.GONE
        }

    }

    override fun onStart() {
        super.onStart()

        if (!startedOnce) {
            onMapOrientation(initialMapRotation, initialMapTilt)
            startedOnce = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun setTitle(text: CharSequence?) {
        binding.titleLabel.text = text
    }

    protected fun getCurrentTitle(): CharSequence = binding.titleLabel.text

    protected fun setTitleHintLabel(text: CharSequence?) {
        binding.titleHintLabel.isGone = text == null
        binding.titleHintLabel.text = text
    }

    protected fun setHint(text: CharSequence?) {
        binding.infoText.isGone = text == null
        binding.infoText.text = text
        updateInfoButtonVisibility()
    }

    protected fun setHintImages(images: List<Drawable>) {
        binding.infoPictures.isGone = images.isEmpty()
        binding.infoPictures.removeAllViews()
        for (image in images) {
            val imageView = ImageView(requireContext())
            imageView.setImageDrawable(image)
            imageView.scaleType
            binding.infoPictures.addView(imageView)
        }
        updateInfoButtonVisibility()
    }

    private fun toggleInfoArea() {
        infoIsExpanded = !infoIsExpanded
        binding.infoButton.setImageResource(
            if (infoIsExpanded) R.drawable.ic_info_filled_24dp
            else R.drawable.ic_info_outline_24dp
        )
        binding.infoButton.isActivated = infoIsExpanded
        binding.infoArea.isGone = !infoIsExpanded
    }

    private fun updateInfoButtonVisibility() {
        binding.infoButton.isGone = binding.infoText.isGone && binding.infoPictures.isGone
    }

    /** Inflate given layout resource id into the content view and return the inflated view */
    protected fun setContentView(resourceId: Int): View {
        if (binding.content.childCount > 0) {
            binding.content.removeAllViews()
        }
        binding.content.visibility = View.VISIBLE
        updateContentPadding()
        layoutInflater.inflate(resourceId, binding.content)
        return binding.content.getChildAt(0)
    }

    protected fun setLocked(locked: Boolean) {
        binding.glassPane.isGone = !locked
    }

    private fun updateContentPadding() {
        if (!contentPadding) {
            binding.content.setPadding(0, 0, 0, 0)
        } else {
            val horizontal = resources.getDimensionPixelSize(R.dimen.quest_form_horizontal_padding)
            val vertical = resources.getDimensionPixelSize(R.dimen.quest_form_vertical_padding)
            binding.content.setPadding(horizontal, vertical, horizontal, vertical)
        }
    }

    protected fun setButtonPanelAnswers(buttonPanelAnswers: List<IAnswerItem>) {
        binding.buttonPanel.removeAllViews()
        for (buttonPanelAnswer in buttonPanelAnswers) {
            val button = ButtonPanelButtonBinding.inflate(layoutInflater, binding.buttonPanel, true).root
            button.setText(buttonPanelAnswer.title)
            button.setOnClickListener { buttonPanelAnswer.action() }
        }
    }

    protected fun checkIsFormComplete() {
        if (isFormComplete()) {
            binding.okButtonContainer.popIn()
        } else {
            binding.okButtonContainer.popOut()
        }
    }

    override fun isRejectingClose() = isFormComplete()

    protected open fun isFormComplete(): Boolean = false

    @AnyThread override fun onMapOrientation(rotation: Float, tilt: Float) {
        // default empty implementation
    }

    protected open fun onClickOk() {}

    protected inline fun <reified T : ViewBinding> contentViewBinding(
        noinline viewBinder: (View) -> T
    ) = FragmentViewBindingPropertyDelegate(this, viewBinder, R.id.content)

    companion object {
        private const val ARG_QUEST_KEY = "quest_key"
        private const val ARG_GEOMETRY = "geometry"
        private const val ARG_QUESTTYPE = "quest_type"
        private const val ARG_MAP_ROTATION = "map_rotation"
        private const val ARG_MAP_TILT = "map_tilt"

        fun createArguments(questKey: QuestKey, questType: QuestType, geometry: ElementGeometry, rotation: Float, tilt: Float) = bundleOf(
            ARG_QUEST_KEY to Json.encodeToString(questKey),
            ARG_GEOMETRY to Json.encodeToString(geometry),
            ARG_QUESTTYPE to questType.name,
            ARG_MAP_ROTATION to rotation,
            ARG_MAP_TILT to tilt
        )
    }
}

interface IAnswerItem {
    val title: Text
    val action: () -> Unit
}

data class AnswerItem(val titleResourceId: Int, override val action: () -> Unit) : IAnswerItem {
    override val title: Text get() = ResText(titleResourceId)
}

data class AnswerItem2(val titleString: String, override val action: () -> Unit) : IAnswerItem {
    override val title: Text get() = CharSequenceText(titleString)
}
