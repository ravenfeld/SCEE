package de.westnordost.streetcomplete.overlays.trail_visibility

import android.os.Bundle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.westnordost.streetcomplete.data.osm.edits.update_tags.StringMapChangesBuilder
import de.westnordost.streetcomplete.data.osm.edits.update_tags.UpdateElementTagsAction
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.TrailVisibility
import de.westnordost.streetcomplete.osm.descriptionResId
import de.westnordost.streetcomplete.osm.titleResId
import de.westnordost.streetcomplete.osm.updateWithCheckDate
import de.westnordost.streetcomplete.overlays.AItemSelectOverlayForm
import de.westnordost.streetcomplete.ui.common.item_select.ImageWithDescription
import kotlinx.serialization.serializer


class TrailVisibilityOverlayForm : AItemSelectOverlayForm<TrailVisibility>() {

    override val items = TrailVisibility.entries
    override val itemsPerRow = 1
    override val serializer = serializer<TrailVisibility>()

    private var originalTrailVisibility: TrailVisibility? = null

    @Composable override fun ItemContent(item: TrailVisibility) {
        ImageWithDescription(
            painter = null,
            title = stringResource(item.titleResId),
            description = stringResource(item.descriptionResId)
        )
    }

    @Composable override fun LastPickedItemContent(item: TrailVisibility) {
        Text(stringResource(item.titleResId))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        originalTrailVisibility = TrailVisibility.entries.find { it.osmValue == element!!.tags["trail_visibility"] }
        selectedItem.value = originalTrailVisibility
    }

    override fun hasChanges(): Boolean = selectedItem.value?.osmValue != originalTrailVisibility?.osmValue

    override fun onClickOk(selectedItem: TrailVisibility) {
        val tagChanges = StringMapChangesBuilder(element!!.tags)
        tagChanges.updateWithCheckDate("trail_visibility", selectedItem.osmValue)
        applyEdit(
            UpdateElementTagsAction(
                element!!,
                tagChanges.create()
            )
        )
    }
}
