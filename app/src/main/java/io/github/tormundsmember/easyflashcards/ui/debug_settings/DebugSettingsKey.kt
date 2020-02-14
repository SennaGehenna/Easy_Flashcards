package io.github.tormundsmember.easyflashcards.ui.debug_settings

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
class DebugSettingsKey(private val id: String = "DebugSettingsKey") : BaseKey() {

    override fun createFragment() = DebugSettingsFragment()
}