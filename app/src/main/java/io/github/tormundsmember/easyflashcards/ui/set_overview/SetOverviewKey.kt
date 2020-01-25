package io.github.tormundsmember.easyflashcards.ui.set_overview

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SetOverviewKey(private val id: String = System.currentTimeMillis().toString()) : BaseKey() {

    override fun createFragment(): BaseFragment {
        return SetOverviewFragment()
    }
}