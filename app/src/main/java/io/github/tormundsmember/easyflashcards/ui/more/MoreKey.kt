package io.github.tormundsmember.easyflashcards.ui.more

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MoreKey(private val id: Int = 0) : BaseKey() {

    override fun createFragment(): BaseFragment = MoreFragment()

}