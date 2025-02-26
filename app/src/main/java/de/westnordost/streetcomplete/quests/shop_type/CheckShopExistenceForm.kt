package de.westnordost.streetcomplete.quests.shop_type

import de.westnordost.streetcomplete.R
import de.westnordost.streetcomplete.quests.AbstractOsmQuestForm
import de.westnordost.streetcomplete.quests.AnswerItem

class CheckShopExistenceForm : AbstractOsmQuestForm<Unit>() {
    override val buttonPanelAnswers = listOf(
        AnswerItem(R.string.quest_generic_hasFeature_no) { replacePlace(false) }, // false to avoid showing edit with "edits in context of" changeset message
        AnswerItem(R.string.quest_generic_hasFeature_yes) { applyAnswer(Unit) },
    )
}
