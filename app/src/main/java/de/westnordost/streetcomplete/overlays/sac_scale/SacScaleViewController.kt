package de.westnordost.streetcomplete.overlays.sac_scale

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.isGone
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.osm.SacScale
import de.westnordost.streetcomplete.osm.asItem
import de.westnordost.streetcomplete.osm.toItems
import de.westnordost.streetcomplete.view.image_select.DisplayItem
import de.westnordost.streetcomplete.view.image_select.ImageListPickerDialog
import de.westnordost.streetcomplete.view.image_select.ItemViewHolder

class SacScaleViewController(
    private val selectButton: ViewGroup,
    private val selectedCellView: ViewGroup,
    private val selectTextView: TextView,
    selectableSurfaces: List<SacScale>
) {
    var value: SacScale?
        set(value) {
            selectedSurfaceItem = value?.asItem()
        }
        get() {
            return selectedSurfaceItem?.value
        }

    private var selectedSurfaceItem: DisplayItem<SacScale>? = null
        set(value) {
            field = value
            updateSelectedCell()
        }

    private val cellLayoutId: Int = R.layout.cell_labeled_icon_select
    private val dialogCellLayoutId: Int = R.layout.cell_labeled_icon_select_sac_scale
    private val items: List<DisplayItem<SacScale>> = selectableSurfaces.toItems()

    var onInputChanged: (() -> Unit)? = null

    init {
        selectButton.setOnClickListener {
            collectSacScaleData { sacScale: SacScale ->
                selectedSurfaceItem = sacScale.asItem()
                onInputChanged?.invoke()
            }
        }

        LayoutInflater.from(selectButton.context).inflate(cellLayoutId, selectedCellView, true)
        selectButton.children.first().background = null
    }

    private fun updateSelectedCell() {
        val item = selectedSurfaceItem
        selectTextView.isGone = item != null
        selectedCellView.isGone = item == null
        if (item != null) {
            ItemViewHolder(selectedCellView).bind(item)
        }
    }

    private fun collectSacScaleData(callback: (SacScale) -> Unit) {
        ImageListPickerDialog(selectButton.context, items, dialogCellLayoutId, 1) { item ->
            callback(item.value!!)
        }.show()
    }
}
