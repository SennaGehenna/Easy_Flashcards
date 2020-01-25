package io.github.tormundsmember.easyflashcards.ui.play

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PlayKey(val setIds: @RawValue List<Int>, val reverseCards: Boolean) : BaseKey() {

    constructor(setId: Int, reverseCards: Boolean) : this(listOf(setId), reverseCards)

    override fun createFragment(): BaseFragment = PlayFragment()
}