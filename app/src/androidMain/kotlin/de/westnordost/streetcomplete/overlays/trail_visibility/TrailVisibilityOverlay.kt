package de.westnordost.streetcomplete.overlays.trail_visibility

import androidx.compose.ui.graphics.Color
import de.westnordost.streetcomplete.R
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
import de.westnordost.streetcomplete.osm.TrailVisibility
import de.westnordost.streetcomplete.resources.*

class TrailVisibilityOverlay : Overlay, AndroidOverlay {

    override val title = Res.string.overlay_trail_visibility_title
    override val icon = R.drawable.ic_quest_trail_visibility
    override val changesetComment = "Specify Trail Visibility"
    override val wikiLink: String = "Key:trail_visibility"
    override val achievements = listOf(OUTDOORS)
    override val defaultDisabledMessage = Res.string.default_disabled_overlay_domain_expert

    override fun getStyledElements(mapData: MapDataWithGeometry) =
        mapData.filter("""
        ways with
          highway ~ path|footway|cycleway|bridleway
          and (
                access !~ no|private
                or foot ~ yes|permissive|designated
                or bicycle ~ yes|permissive|designated
              )
        """).map { it to getStyle(it) }

    override fun createForm(element: Element?) = TrailVisibilityOverlayForm()

    private fun getStyle(element: Element): OverlayStyle {
        val trailVisibility = TrailVisibility.entries.find { it.osmValue == element.tags["trail_visibility"] }

        return OverlayStyle.Polyline(
            stroke = trailVisibility.color.let { OverlayStyle.Stroke(it) },
            label = trailVisibility?.name
        )
    }

    private val TrailVisibility?.color
        get() = when (this) {
            TrailVisibility.EXCELLENT -> Color(0xff8CC63E)
            TrailVisibility.GOOD -> Color(0xff00B2E6)
            TrailVisibility.INTERMEDIATE -> Color(0xffFECB1B)
            TrailVisibility.BAD -> Color(0xffF47922)
            TrailVisibility.HORRIBLE -> Color(0xff874D99)
            TrailVisibility.NO -> Color(0xff000000)
            null -> OverlayColor.Red
        }
}

