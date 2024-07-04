package de.westnordost.streetcomplete.overlays.sac_scale

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import com.russhwolf.settings.ObservableSettings
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.edits.update_tags.UpdateElementTagsAction
import de.westnordost.streetcomplete.databinding.FragmentOverlaySacScaleSelectBinding
import de.westnordost.streetcomplete.overlays.AbstractOverlayForm
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.asItem
import de.westnordost.streetcomplete.util.LastPickedValuesStore
import de.westnordost.streetcomplete.util.ktx.valueOfOrNull
import de.westnordost.streetcomplete.view.setImage
import org.koin.android.ext.android.inject

class SacScaleOverlayForm : AbstractOverlayForm() {

    override val contentLayoutResId = R.layout.fragment_overlay_sac_scale_select
    private val binding by contentViewBinding(FragmentOverlaySacScaleSelectBinding::bind)

    private var originalSacScale: SacScale? = null

    private lateinit var sacScaleCtrl: SacScaleViewController
    private val prefs: ObservableSettings by inject()
    private lateinit var favs: LastPickedValuesStore<SacScale>
    private val lastPickedSurface: SacScale?
        get() = favs.get().firstOrNull()

    override fun hasChanges(): Boolean = sacScaleCtrl.value != originalSacScale

    override fun isFormComplete(): Boolean = sacScaleCtrl.value != null

    override fun onClickOk() {
        val changesBuilder = StringMapChangesBuilder(element!!.tags)

        if (sacScaleCtrl.value != null) {
            favs.add(sacScaleCtrl.value!!)
        }
        changesBuilder["sac_scale"] = sacScaleCtrl.value!!.osmValue

        applyEdit(UpdateElementTagsAction(element!!, changesBuilder.create()))
    }


    override fun onAttach(ctx: Context) {
        super.onAttach(ctx)
        favs = LastPickedValuesStore(
            prefs,
            key = javaClass.simpleName,
            serialize = { it.name },
            deserialize = { valueOfOrNull<SacScale>(it) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        originalSacScale = SacScale.entries.find { it.osmValue == element!!.tags["sac_scale"] }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sacScaleCtrl = SacScaleViewController(
            binding.selectButton.root,
            binding.selectButton.selectedCellView,
            binding.selectButton.selectTextView,
            SacScale.entries
        )
        sacScaleCtrl.onInputChanged = { checkIsFormComplete() }

        binding.lastPickedButton.isGone = lastPickedSurface == null
        binding.lastPickedButton.setImage(lastPickedSurface?.asItem()?.image)
        binding.lastPickedButton.setOnClickListener {
            sacScaleCtrl.value = lastPickedSurface
            binding.lastPickedButton.isGone = true
            checkIsFormComplete()
        }

        if (savedInstanceState != null) {
            onLoadInstanceState(savedInstanceState)
        } else {
            initStateFromTags()
        }
        checkIsFormComplete()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAC_SCALE, sacScaleCtrl.value?.osmValue)

    }

    private fun onLoadInstanceState(inState: Bundle) {
        sacScaleCtrl.value = SacScale.entries.find { it.osmValue == inState.getString(SAC_SCALE) }
    }

    private fun initStateFromTags() {
        sacScaleCtrl.value = originalSacScale
    }

    companion object {
        private const val SAC_SCALE = "selected_sac_scale"
    }
}
