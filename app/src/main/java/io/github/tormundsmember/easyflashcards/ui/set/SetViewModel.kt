package io.github.tormundsmember.easyflashcards.ui.set

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetViewModel : BaseViewModel() {

    private var setId: Int? = null

    val cards: LiveData<List<Card>>
        get() = Dependencies.database.observeSet(
            setId ?: throw IllegalStateException("setId is null. Did you forget to set init(setId: Int)?")
        )


    fun init(setId: Int) {
        this.setId = setId
    }

    fun flipCards(selectedItems: List<Card>) {
        Dependencies.database.addOrUpdateCards(selectedItems.map { it.copy(backText = it.frontText, frontText = it.backText) })
    }

    fun resetProgress(selectedItems: List<Card>): LiveData<LoadingState> {
        return MutableLiveData(LoadingState.NotStarted).apply {
            postValue(LoadingState.Loading)
//            CoroutineScope(Dispatchers.IO).launch {
                val updatedCards = selectedItems.flatMap {
                    Dependencies.database.getCardsBySetId(it.setId)
                }.map {
                    it.copy(currentInterval = RehearsalInterval.STAGE_1, nextRecheck = 0)
                }
                Dependencies.database.addOrUpdateCards(updatedCards)
                postValue(LoadingState.Done)
//            }
        }
    }

}