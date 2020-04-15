package io.github.tormundsmember.easyflashcards.ui.search

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchKey(private val id: String = SearchKey::class.java.simpleName) : BaseKey() {

    override fun createFragment() = SearchFragment()

}