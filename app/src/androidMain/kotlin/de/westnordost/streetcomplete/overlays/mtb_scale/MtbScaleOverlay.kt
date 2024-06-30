package de.westnordost.streetcomplete.overlays.mtb_scale

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
import de.westnordost.streetcomplete.osm.mtb_scale.MtbScale
import de.westnordost.streetcomplete.osm.mtb_scale.parseMtbScale
import de.westnordost.streetcomplete.osm.surface.UNPAVED_SURFACES
import de.westnordost.streetcomplete.resources.*

class MtbScaleOverlay : Overlay, AndroidOverlay {

    override val title = Res.string.overlay_mtb_scale
    override val icon = R.drawable.quest_mtb
    override val changesetComment = "Specify MTB difficulty"
    override val wikiLink: String = "Key:mtb:scale"
    override val achievements = listOf(BICYCLIST, OUTDOORS)
    override val defaultDisabledMessage = Res.string.default_disabled_overlay_domain_expert

    override fun getStyledElements(mapData: MapDataWithGeometry) =
        mapData.filter("""
            ways with
              highway ~ path|track|bridleway
              and (
                access !~ no|private
                or foot ~ yes|permissive|designated
                or bicycle ~ yes|permissive|designated
              )
              and mtb != no
        """).map { it to getStyle(it) }

    override fun createForm(element: Element?) = MtbScaleOverlayForm()

    private fun getStyle(element: Element): OverlayStyle {
        val mtbScale = parseMtbScale(element.tags)
        val color = mtbScale.color

        return OverlayStyle.Polyline(
            stroke =  OverlayStyle.Stroke(color) ,
            label = mtbScale?.value?.toString()
        )
    }
}

private val MtbScale?.color get() = when (this?.value) {
    MtbScale.Value.ZERO -> OverlayColor.Blue
    MtbScale.Value.ONE -> OverlayColor.Cyan
    MtbScale.Value.TWO -> OverlayColor.Lime
    MtbScale.Value.THREE -> OverlayColor.Gold
    MtbScale.Value.FOUR -> OverlayColor.Orange
    MtbScale.Value.FIVE -> OverlayColor.Purple
    MtbScale.Value.SIX -> OverlayColor.Black
    else -> OverlayColor.Red
}
