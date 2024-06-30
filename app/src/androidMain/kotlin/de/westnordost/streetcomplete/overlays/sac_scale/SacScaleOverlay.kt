package de.westnordost.streetcomplete.overlays.sac_scale

import androidx.compose.ui.graphics.Color
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.data.elementfilter.toElementFilterExpression
import de.westnordost.streetcomplete.data.osm.mapdata.Element
import de.westnordost.streetcomplete.data.osm.mapdata.MapDataWithGeometry
import de.westnordost.streetcomplete.data.osm.mapdata.filter
import de.westnordost.streetcomplete.data.overlays.AndroidOverlay
import de.westnordost.streetcomplete.data.overlays.Overlay
import de.westnordost.streetcomplete.data.overlays.OverlayColor
import de.westnordost.streetcomplete.data.overlays.OverlayStyle
import de.westnordost.streetcomplete.data.user.achievements.EditTypeAchievement.*
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.SacScale.ALPINE_HIKING
import de.westnordost.streetcomplete.osm.SacScale.DEMANDING_ALPINE_HIKING
import de.westnordost.streetcomplete.osm.SacScale.DEMANDING_MOUNTAIN_HIKING
import de.westnordost.streetcomplete.osm.SacScale.DIFFICULT_ALPINE_HIKING
import de.westnordost.streetcomplete.osm.SacScale.HIKING
import de.westnordost.streetcomplete.osm.SacScale.MOUNTAIN_HIKING
import de.westnordost.streetcomplete.osm.SacScale.STROLLING
import de.westnordost.streetcomplete.osm.mtb_scale.MtbScale
import de.westnordost.streetcomplete.osm.mtb_scale.parseMtbScale
import de.westnordost.streetcomplete.osm.surface.UNPAVED_SURFACES
import de.westnordost.streetcomplete.resources.*

class SacScaleOverlay : Overlay, AndroidOverlay {

    override val title = Res.string.overlay_sac_scale
    override val icon = R.drawable.ic_quest_sac_scale
    override val changesetComment = "Specify sac scale"
    override val wikiLink: String = "Key:sac_scale"
    override val achievements = listOf(OUTDOORS)
    override val defaultDisabledMessage = Res.string.default_disabled_overlay_domain_expert

    override fun getStyledElements(mapData: MapDataWithGeometry) =
        mapData.filter("""
        ways with
          highway ~ path|track|bridleway
          and (
                access !~ no|private
                or foot ~ yes|permissive|designated
              )
        """).map { it to getStyle(it) }

    override fun createForm(element: Element?) = SacScaleOverlayForm()

    private fun getStyle(element: Element): OverlayStyle {
        val sacScale = SacScale.entries.find { it.osmValue == element.tags["sac_scale"] }

        return OverlayStyle.Polyline(
            stroke = sacScale.color.let { OverlayStyle.Stroke(it) },
            label = sacScale?.name
        )
    }

    private val SacScale?.color
        get() = when (this) {
            STROLLING -> Color(0xfffaf3a5)
            HIKING -> Color(0xff8CC63E)
            MOUNTAIN_HIKING -> Color(0xff00B2E6)
            DEMANDING_MOUNTAIN_HIKING -> Color(0xffFECB1B)
            ALPINE_HIKING -> Color(0xffF47922)
            DEMANDING_ALPINE_HIKING -> Color(0xff874D99)
            DIFFICULT_ALPINE_HIKING -> Color(0xff000000)
            null -> OverlayColor.Red
        }
}

