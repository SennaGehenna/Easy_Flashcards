package io.github.tormundsmember.easyflashcards.ui.settings

import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import kotlinx.android.parcel.Parcelize

@Parcelize
class SettingsKey(private val id: String = SettingsKey::class.java.simpleName) : BaseKey() {
    override fun createFragment() = SettingsFragment()
}