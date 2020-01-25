package io.github.tormundsmember.easyflashcards.ui.set

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SetKey(val setId: Int) : BaseKey() {

    override fun createFragment() = SetFragment()
}