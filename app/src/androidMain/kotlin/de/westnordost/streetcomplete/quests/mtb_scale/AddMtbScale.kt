package de.westnordost.streetcomplete.quests.mtb_scale

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression
import de.westnordost.streetcomplete.data.osm.geometry.ElementGeometry
import de.westnordost.streetcomplete.data.osm.mapdata.Element
import de.westnordost.streetcomplete.data.osm.mapdata.ElementType
import de.westnordost.streetcomplete.data.osm.mapdata.MapData
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataWithGeometry
import de.westnordost.streetcomplete.data.osm.mapdata.Way
import de.westnordost.streetcomplete.data.osm.mapdata.filter
import de.westnordost.streetcomplete.data.osm.osmquests.OsmElementQuestType
import de.westnordost.streetcomplete.data.osm.osmquests.OsmFilterQuestType
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.Tags
import de.westnordost.streetcomplete.data.quest.AndroidQuest
import de.westnordost.streetcomplete.osm.mtb_scale.MtbScale
import de.westnordost.streetcomplete.osm.mtb_scale.applyTo
import de.westnordost.streetcomplete.osm.surface.UNPAVED_SURFACES
import de.westnordost.streetcomplete.quests.BooleanQuestSettingsDialog
import de.westnordost.streetcomplete.quests.FullElementSelectionDialog
import de.westnordost.streetcomplete.quests.getPrefixedFullElementSelectionPref
import de.westnordost.streetcomplete.quests.questPrefix
import de.westnordost.streetcomplete.resources.Res
import de.westnordost.streetcomplete.resources.default_disabled_msg_sacScale
import de.westnordost.streetcomplete.resources.quest_mtbScale_title
import de.westnordost.streetcomplete.resources.quest_sacScale_title
import de.westnordost.streetcomplete.ui.common.dialogs.InfoDialog

class AddMtbScale : OsmFilterQuestType<MtbScale>(), AndroidQuest {

    override val elementFilter = """
        ways with
          highway ~ path|track|bridleway
          and !mtb:scale
          and (
                access !~ no|private
                or foot ~ yes|permissive|designated
                or bicycle ~ yes|permissive|designated
          )
          and mtb != no
          and (
                surface ~ ${UNPAVED_SURFACES.joinToString("|")}|wood
                or (highway = track and tracktype and tracktype != grade1)
          )
    """

    override val changesetComment = "Specify MTB Scale"
    override val wikiLink = "Key:mtb:scale"
    override val icon = R.drawable.quest_mtb
    override val title = Res.string.quest_mtbScale_title
    override val defaultDisabledMessage = Res.string.default_disabled_msg_sacScale


    override fun getHighlightedElements(element: Element, mapData: MapDataWithGeometry) =
        mapData.filter("ways with highway and mtb:scale")

    override fun createForm() = AddMtbScaleForm()

    override fun applyAnswerTo(answer: MtbScale, tags: Tags, geometry: ElementGeometry, timestampEdited: Long) {
        answer.applyTo(tags)
    }
}

