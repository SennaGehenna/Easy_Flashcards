package io.github.tormundsmember.easyflashcards.ui.licenses

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LicensesKey(private val id: Int = 0) : BaseKey() {
    override fun createFragment(): BaseFragment = LicensesFragment()
}