package io.github.tormundsmember.easyflashcards.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.util.plusAssign
import java.util.concurrent.atomic.AtomicReference

class SearchViewModel : BaseViewModel() {

    private val _searchResults: MutableLiveData<List<Card>> = MutableLiveData()
    val searchResults: LiveData<List<Card>>
        get() = _searchResults

    private val lastSearchTerm = AtomicReference("")

    fun search(term: String) {
        if (term.length >= 3) {
            lastSearchTerm.set(term)
            val cardsByPartialName = Dependencies.database.getCardsBySearchTerm(term)
            if (term == lastSearchTerm.get()) {
                _searchResults += cardsByPartialName
            }
        } else {
            lastSearchTerm.set("")
            _searchResults += emptyList()
        }
    }

}