package io.github.tormundsmember.easyflashcards.ui.set

import androidx.lifecycle.LiveData
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel

class SetViewModel : BaseViewModel() {

    private var setId: Int? = null

    val cards: LiveData<List<Card>>
        get() = Dependencies.database.observeSet(setId!!)


    fun init(setId: Int) {
        this.setId = setId
    }

}