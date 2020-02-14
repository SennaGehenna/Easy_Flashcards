package io.github.tormundsmember.easyflashcards.ui.debug_settings

import android.os.Bundle
import android.view.View
import android.widget.Switch
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment

class DebugSettingsFragment : BaseFragment() {

    override val layoutId: Int = R.layout.screen_debug_settings

    private lateinit var switchHasSeenAllSetsTutorial: Switch
    private lateinit var switchHasSeenAllSetsTutorialWithExistingItems: Switch
    private lateinit var switchHasSeenOverviewTutorial: Switch
    private lateinit var switchHasSeenOverviewTutorialWithExistingItems: Switch

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchHasSeenAllSetsTutorial = view.findViewById(R.id.hasSeenTutorial)
        switchHasSeenAllSetsTutorialWithExistingItems = view.findViewById(R.id.hasSeenTutorialWithExistingItems)
        switchHasSeenOverviewTutorial = view.findViewById(R.id.hasSeenOverviewTutorial)
        switchHasSeenOverviewTutorialWithExistingItems = view.findViewById(R.id.hasSeenOverviewTutorialWithExistingItems)

        with(Dependencies.userData) {
            switchHasSeenAllSetsTutorial.isChecked = hasSeenSetsTutorial
            switchHasSeenAllSetsTutorial.setOnCheckedChangeListener { _, isChecked ->
                hasSeenSetsTutorial = isChecked
            }

            switchHasSeenAllSetsTutorialWithExistingItems.isChecked = hasSeenSetsTutorialWithExistingItems
            switchHasSeenAllSetsTutorialWithExistingItems.setOnCheckedChangeListener { _, isChecked ->
                hasSeenSetsTutorialWithExistingItems = isChecked
            }

            switchHasSeenOverviewTutorial.isChecked = hasSeenSetOverviewTutorial
            switchHasSeenOverviewTutorial.setOnCheckedChangeListener { _, isChecked ->
                hasSeenSetOverviewTutorial = isChecked
            }

            switchHasSeenOverviewTutorialWithExistingItems.isChecked = hasSeenSetOverviewTutorialWithExistingItems
            switchHasSeenOverviewTutorialWithExistingItems.setOnCheckedChangeListener { _, isChecked ->
                hasSeenSetOverviewTutorialWithExistingItems = isChecked
            }
        }
    }
}