package io.github.tormundsmember.easyflashcards.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.zhuinden.simplestack.BackstackDelegate
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.BackstackHandler
import io.github.tormundsmember.easyflashcards.ui.base_ui.FragmentStateChanger
import io.github.tormundsmember.easyflashcards.ui.set_overview.SetOverviewKey

/**
 * design available in https://projects.invisionapp.com/freehand/document/Qjl32e0Ze
 */
class MainActivity : AppCompatActivity(), StateChanger, BackstackHandler {

    private lateinit var fragmentStateChanger: FragmentStateChanger
    override fun getBackstack() = backstackDelegate.backstack
    private lateinit var backstackDelegate: BackstackDelegate

    override fun onCreate(savedInstanceState: Bundle?) {

        Dependencies.init(this)

        setDarkMode()

        backstackDelegate = BackstackDelegate()
        @Suppress("DEPRECATION")
        backstackDelegate.onCreate(savedInstanceState, lastCustomNonConfigurationInstance, History.of(SetOverviewKey()))
        backstackDelegate.registerForLifecycleCallbacks(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.fragmentContainer)
        backstackDelegate.setStateChanger(this)

    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.topNewState<Any>() == stateChange.topPreviousState<Any>()) {
            completionCallback.stateChangeComplete()
            return
        }
        fragmentStateChanger.handleStateChange(stateChange)
        completionCallback.stateChangeComplete()
    }

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) {
            super.onBackPressed()
        }
    }

    fun setDarkMode() {
        if (Dependencies.userData.useDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
