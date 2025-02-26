package de.westnordost.streetcomplete.screens.settings

import de.westnordost.streetcomplete.screens.settings.debug.ShowQuestFormsViewModel
import de.westnordost.streetcomplete.screens.settings.debug.ShowQuestFormsViewModelImpl
import de.westnordost.streetcomplete.screens.settings.questselection.QuestPresetsViewModel
import de.westnordost.streetcomplete.screens.settings.questselection.QuestPresetsViewModelImpl
import de.westnordost.streetcomplete.screens.settings.questselection.QuestSelectionViewModel
import de.westnordost.streetcomplete.screens.settings.questselection.QuestSelectionViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule = module {
    single { ResurveyIntervalsUpdater(get()) }

    viewModel<SettingsViewModel> { SettingsViewModelImpl(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel<QuestSelectionViewModel> { QuestSelectionViewModelImpl(get(), get(), get(), get(), get(named("CountryBoundariesLazy")), get()) }
    viewModel<QuestPresetsViewModel> { QuestPresetsViewModelImpl(get(), get(), get(), get()) }
    viewModel<ShowQuestFormsViewModel> { ShowQuestFormsViewModelImpl(get(), get()) }
}
