package io.github.tormundsmember.easyflashcards.ui.set_overview

import androidx.lifecycle.LiveData
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set

class SetOverviewViewModel : BaseViewModel() {

    val sets: LiveData<List<Set>>
        get() = Dependencies.database.observeSets()

}