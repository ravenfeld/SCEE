package de.westnordost.streetcomplete.quests.surface

import androidx.appcompat.app.AlertDialog
import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.quests.AImageListQuestAnswerFragment
import de.westnordost.streetcomplete.quests.AnswerItem
import de.westnordost.streetcomplete.view.image_select.Item

class AddRoadSurfaceForm : AImageListQuestAnswerFragment<Surface, SurfaceOrIsStepsAnswer>() {
    override val otherAnswers = listOf(
        AnswerItem(R.string.quest_way_private) { applyAnswer(IsPrivateAnswer) }
    )

    override val items: List<Item<Surface>>
        get() = (PAVED_SURFACES + UNPAVED_SURFACES + GROUND_SURFACES + GENERIC_SURFACES).toItems()

    override val itemsPerRow = 3

    override fun onClickOk(selectedItems: List<Surface>) {
        val value = selectedItems.single()
        if (value.shouldBeDescribed) {
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.quest_surface_detailed_answer_impossible_confirmation)
                .setPositiveButton(R.string.quest_generic_confirmation_yes) { _, _ ->
                    DescribeGenericSurfaceDialog(requireContext()) { description ->
                        applyAnswer(SurfaceAnswer(value, description))
                    }.show()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
            return
        }
        applyAnswer(SurfaceAnswer(value))
    }
}
