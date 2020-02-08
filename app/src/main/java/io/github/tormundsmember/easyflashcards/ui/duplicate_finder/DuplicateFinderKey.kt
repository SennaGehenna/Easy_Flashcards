package io.github.tormundsmember.easyflashcards.ui.duplicate_finder

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DuplicateFinderKey(private val id: String = DuplicateFinderKey::class.java.simpleName) : BaseKey() {

    override fun createFragment() = DuplicateFinderFragment()
}