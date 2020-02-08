package io.github.tormundsmember.easyflashcards.ui.duplicate_finder

import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card

class DuplicateFinderViewModel : BaseViewModel() {

    val duplicateCards: List<Card> = Dependencies.database.getDuplicates()

}