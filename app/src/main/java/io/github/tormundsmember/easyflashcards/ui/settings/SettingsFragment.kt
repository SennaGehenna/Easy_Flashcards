package io.github.tormundsmember.easyflashcards.ui.settings

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.widget.Switch
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.MainActivity
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.util.openUrlInCustomTabs
import io.github.tormundsmember.easyflashcards.ui.util.prepareLinkText
import io.github.tormundsmember.easyflashcards.ui.util.putCursorInTextview

class SettingsFragment : BaseFragment() {
    override val layoutId: Int = R.layout.screen_settings
    override val titleText: String
        get() = getString(R.string.settings)

    private lateinit var switchDarkMode: Switch
    private lateinit var switchSpatialRepetition: Switch
    private lateinit var hintSpatialRepetition: AppCompatTextView
    private lateinit var switchCrashUsageData: Switch
    private lateinit var switchLimitCards: Switch
    private lateinit var txtCardLimit: AppCompatEditText


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchSpatialRepetition = view.findViewById(R.id.switchSpatialRepetition)
        hintSpatialRepetition = view.findViewById(R.id.hintSpatialRepetition)
        switchCrashUsageData = view.findViewById(R.id.switchCrashUsageData)
        switchLimitCards = view.findViewById(R.id.switchLimitCards)
        txtCardLimit = view.findViewById(R.id.txtCardLimit)

        switchCrashUsageData.text = getString(R.string.enableCrashReporting).prepareLinkText(view.context)
        switchCrashUsageData.isChecked = Dependencies.userData.allowCrashReporting
        switchCrashUsageData.setOnCheckedChangeListener { _, isChecked ->
            Dependencies.userData.allowCrashReporting = isChecked
        }

        switchDarkMode.isChecked = Dependencies.userData.useDarkMode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            Dependencies.userData.useDarkMode = isChecked
            (activity as MainActivity?)?.setDarkMode()
        }

        switchSpatialRepetition.isChecked = Dependencies.userData.useSpacedRepetition
        switchSpatialRepetition.setOnCheckedChangeListener { _, isChecked ->
            Dependencies.userData.useSpacedRepetition = isChecked
        }

        with(Dependencies.userData) {
            txtCardLimit.isEnabled = limitCards
            switchLimitCards.isChecked = limitCards
        }
        switchLimitCards.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                txtCardLimit.putCursorInTextview(true)
            }
            Dependencies.userData.limitCards = isChecked
            txtCardLimit.isEnabled = isChecked
        }

        txtCardLimit.setText(Dependencies.userData.limitCardsAmount.takeIf { it > 0 }?.toString() ?: "")

        hintSpatialRepetition.text = getString(R.string.explanationSpacedRepetition).prepareLinkText(view.context)
        hintSpatialRepetition.setOnClickListener {
            openUrlInCustomTabs(it.context, Uri.parse("https://en.wikipedia.org/wiki/Spaced_repetition"))
        }
    }

    override fun onStop() {
        super.onStop()
        val newCardLimit = txtCardLimit.text?.toString()?.toIntOrNull()
        if (newCardLimit != null) {
            Dependencies.userData.limitCardsAmount = newCardLimit
        }
    }
}