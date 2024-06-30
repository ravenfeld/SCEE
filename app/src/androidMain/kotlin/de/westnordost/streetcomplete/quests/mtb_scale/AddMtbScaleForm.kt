package de.westnordost.streetcomplete.quests.mtb_scale

import androidx.compose.runtime.Composable
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.mtb_scale.MtbScale
import de.westnordost.streetcomplete.osm.mtb_scale.description
import de.westnordost.streetcomplete.osm.mtb_scale.icon
import de.westnordost.streetcomplete.osm.mtb_scale.title
import de.westnordost.streetcomplete.quests.AItemSelectQuestForm
import de.westnordost.streetcomplete.ui.common.item_select.ImageWithDescription
import kotlinx.serialization.serializer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class AddMtbScaleForm : AItemSelectQuestForm<MtbScale.Value, MtbScale.Value>() {

    override val items = MtbScale.Value.entries

    override val itemsPerRow = 1

    override val moveFavoritesToFront = false

    override val serializer = serializer<MtbScale.Value>()

    @Composable override fun ItemContent(item: MtbScale.Value) {
        ImageWithDescription(
            painter = painterResource(item.icon),
            title = stringResource(item.title),
            description = stringResource(item.description)
        )
    }

    override fun onClickOk(selectedItem: MtbScale.Value) {
        applyAnswer(selectedItem)
    }
}

