package io.github.tormundsmember.easyflashcards.ui.set_overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.LoadingState
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetOverviewViewModel : BaseViewModel() {
    fun resetProgress(selectedItems: List<Set>): LiveData<LoadingState> {
        return MutableLiveData(LoadingState.NotStarted).apply {
            postValue(LoadingState.Loading)
            CoroutineScope(Dispatchers.IO).launch {
                val updatedCards = selectedItems.flatMap {
                    Dependencies.database.getCardsBySetId(it.id)
                }.map {
                    it.copy(currentInterval = RehearsalInterval.STAGE_1, nextRecheck = 0)
                }
                Dependencies.database.addOrUpdateCards(updatedCards)
                postValue(LoadingState.Done)
            }
        }
    }

    val sets: LiveData<List<Set>>
        get() = Dependencies.database.observeSets()

}