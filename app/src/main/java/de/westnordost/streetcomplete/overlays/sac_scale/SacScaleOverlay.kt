package de.westnordost.streetcomplete.overlays.sac_scale

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.osm.mapdata.Element
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataWithGeometry
import de.westnordost.streetcomplete.data.osm.mapdata.filter

import de.westnordost.streetcomplete.overlays.AbstractOverlayForm
import de.westnordost.streetcomplete.overlays.Color
import de.westnordost.streetcomplete.overlays.Overlay
import de.westnordost.streetcomplete.overlays.PolylineStyle
import de.westnordost.streetcomplete.overlays.StrokeStyle
import de.westnordost.streetcomplete.overlays.Style
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.SacScale.*

class SacScaleOverlay : Overlay {

    override val title = R.string.overlay_sac_scale
    override val icon = R.drawable.ic_quest_sac_scale
    override val changesetComment = "Specify sac scale"
    override val wikiLink: String = "Key:sac_scale"

    override fun getStyledElements(mapData: MapDataWithGeometry): Sequence<Pair<Element, Style>> =
        mapData.filter(
            """
        ways with
          highway ~ path
          and ( access !~ no|private or foot ~ yes|permissive|designated or bicycle ~ yes|permissive|designated)
          and (!lit or lit = no)
          and surface ~ "grass|sand|dirt|soil|fine_gravel|compacted|wood|gravel|pebblestone|rock|ground|earth|mud|woodchips|snow|ice|salt|stone"
        """
        ).map {
            it to getStyle(it)
        }

    override fun createForm(element: Element?): AbstractOverlayForm = SacScaleOverlayForm()

    private fun getStyle(element: Element): Style {
        val color = SacScale.entries.find { it.osmValue == element.tags["sac_scale"] }.color

        return PolylineStyle(StrokeStyle(color))
    }

    private val SacScale?.color
        get() = when (this) {
            HIKING -> "#8CC63E"
            MOUNTAIN_HIKING -> "#00B2E6"
            DEMANDING_MOUNTAIN_HIKING -> "#FECB1B"
            ALPINE_HIKING -> "#F47922"
            DEMANDING_ALPINE_HIKING -> "#874D99"
            DIFFICULT_ALPINE_HIKING -> "#000000"
            null -> Color.DATA_REQUESTED
        }
}
