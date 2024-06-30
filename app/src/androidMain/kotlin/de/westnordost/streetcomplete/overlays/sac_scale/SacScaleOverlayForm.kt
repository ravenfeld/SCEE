package de.westnordost.streetcomplete.overlays.sac_scale

import android.os.Bundle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.edits.update_tags.UpdateElementTagsAction
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.mtb_scale.MtbScale
import de.westnordost.streetcomplete.osm.mtb_scale.applyTo
import de.westnordost.streetcomplete.osm.mtb_scale.description
import de.westnordost.streetcomplete.osm.mtb_scale.icon
import de.westnordost.streetcomplete.osm.mtb_scale.parseMtbScale
import de.westnordost.streetcomplete.osm.mtb_scale.title
import de.westnordost.streetcomplete.osm.updateWithCheckDate
import de.westnordost.streetcomplete.overlays.AItemSelectOverlayForm
import de.westnordost.streetcomplete.overlays.AbstractOverlayForm
import de.westnordost.streetcomplete.ui.common.item_select.ImageWithDescription
import kotlinx.serialization.serializer
import kotlin.text.get


class SacScaleOverlayForm : AItemSelectOverlayForm<SacScale>() {

    override val items = SacScale.entries
    override val itemsPerRow = 1
    override val serializer = serializer<SacScale>()

    private var originalSacScale: SacScale? = null

    @Composable override fun ItemContent(item: SacScale) {
        ImageWithDescription(
            painter = painterResource(item.imageResId),
            title = stringResource(item.titleResId),
            description = stringResource(item.descriptionResId)
        )
    }

    @Composable override fun LastPickedItemContent(item: SacScale) {
        Text(stringResource(item.titleResId))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        originalSacScale = SacScale.entries.find { it.osmValue == element!!.tags["sac_scale"] }
        selectedItem.value = originalSacScale
    }

    override fun hasChanges(): Boolean = selectedItem.value?.osmValue != originalSacScale?.osmValue

    override fun onClickOk(selectedItem: SacScale) {
        val tagChanges = StringMapChangesBuilder(element!!.tags)
        tagChanges.updateWithCheckDate("sac_scale", selectedItem.osmValue)
        applyEdit(
            UpdateElementTagsAction(
                element!!,
                tagChanges.create()
            )
        )
    }
}
