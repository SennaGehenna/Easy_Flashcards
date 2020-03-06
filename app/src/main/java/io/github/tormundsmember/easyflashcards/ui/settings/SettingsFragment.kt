package io.github.tormundsmember.easyflashcards.ui.settings

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.databinding.ScreenSettingsBinding
import io.github.tormundsmember.easyflashcards.ui.BuildVariant
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.MainActivity
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.debug_settings.DebugSettingsKey
import io.github.tormundsmember.easyflashcards.ui.util.openUrlInCustomTabs
import io.github.tormundsmember.easyflashcards.ui.util.prepareLinkText
import io.github.tormundsmember.easyflashcards.ui.util.putCursorInTextview
import io.github.tormundsmember.easyflashcards.ui.util.visible

class SettingsFragment : BaseFragment() {
    override val layoutId: Int = R.layout.screen_settings
    override val titleText: String
        get() = getString(R.string.settings)

    private lateinit var txtCardLimit: AppCompatEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(ScreenSettingsBinding.bind(view)) {

            this@SettingsFragment.txtCardLimit = txtCardLimit

            if (!BuildVariant.isProductionBuild()) {
                txtDebugSettings.visible()
                vDebugSettingsSeparator.visible()
            }

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

            with(Dependencies.userData.useSpacedRepetition) {
                switchSpatialRepetition.isChecked = this
                switchDontRehearseRememberedTerms.isEnabled = this
                hintDontRehearseRememberedTerms.isEnabled = this
            }
            switchSpatialRepetition.setOnCheckedChangeListener { _, isChecked ->
                Dependencies.userData.useSpacedRepetition = isChecked
                switchDontRehearseRememberedTerms.isEnabled = isChecked
                hintDontRehearseRememberedTerms.isEnabled = isChecked
            }

            switchDontRehearseRememberedTerms.isChecked = Dependencies.userData.doNotShowLearnedCards
            switchDontRehearseRememberedTerms.setOnCheckedChangeListener { _, isChecked ->
                Dependencies.userData.doNotShowLearnedCards = isChecked
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
            lblCardLimit.setOnClickListener {
                txtCardLimit.putCursorInTextview(true)
            }

            txtCardLimit.setText(Dependencies.userData.limitCardsAmount.takeIf { it > 0 }?.toString() ?: "")

            hintSpatialRepetition.text = getString(R.string.explanationSpacedRepetition).prepareLinkText(view.context)
            hintSpatialRepetition.setOnClickListener {
                openUrlInCustomTabs(it.context, Uri.parse("https://en.wikipedia.org/wiki/Spaced_repetition"))
            }

            txtDebugSettings.setOnClickListener {
                goTo(DebugSettingsKey())
            }
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