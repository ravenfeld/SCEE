package de.westnordost.streetcomplete.quests.crossing_markings

import androidx.annotation.DrawableRes
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.view.image_select.DisplayItem
import de.westnordost.streetcomplete.view.image_select.Item

enum class CrossingMarkings(
    val osmValue: String,
    @DrawableRes val imageResId: Int?,
) {
    YES(
        osmValue = "yes",
        imageResId = null
    ),
    NO(
        osmValue = "no",
        imageResId = R.drawable.crossing_markings_no
    ),
    ZEBRA(
        osmValue = "zebra",
        imageResId = R.drawable.crossing_markings_zebra
    ),
    LINES(
        osmValue = "lines",
        imageResId = R.drawable.crossing_markings_lines
    ),
    LADDER(
        osmValue = "ladder",
        imageResId = R.drawable.crossing_markings_ladder
    ),
    DASHES(
        osmValue = "dashes",
        imageResId = R.drawable.crossing_markings_dashes
    ),
    DOTS(
        osmValue = "dots",
        imageResId = R.drawable.crossing_markings_dots
    ),
    SURFACE(
        osmValue = "surface",
        imageResId = R.drawable.crossing_markings_surface
    ),
    LADDER_SKEWED(
        osmValue = "ladder:skewed",
        imageResId = R.drawable.crossing_markings_ladder_skewed
    ),
    ZEBRA_PAIRED(
        osmValue = "zebra:paired",
        imageResId = R.drawable.crossing_markings_zebra_paired
    ),
    ZEBRA_BICOLOUR(
        osmValue = "zebra:bicolour",
        imageResId = R.drawable.crossing_markings_zebra_bicolour
    ),
    ZEBRA_DOUBLE(
        osmValue = "zebra:double",
        imageResId = R.drawable.crossing_markings_zebra_double
    ),
    LADDER_PAIRED(
        osmValue = "ladder:paired",
        imageResId = R.drawable.crossing_markings_ladder_paired
    ),
    ZEBRA_DOTS(
        osmValue = "zebra;dots",
        imageResId = R.drawable.crossing_markings_zebra_dots
    ),
    PICTOGRAMS(
        osmValue = "pictograms",
        imageResId = R.drawable.crossing_markings_pictograms
    )
}

fun Collection<CrossingMarkings>.toItems() = map { it.asItem() }

fun CrossingMarkings.asItem(): DisplayItem<CrossingMarkings> = Item(this, imageResId)
