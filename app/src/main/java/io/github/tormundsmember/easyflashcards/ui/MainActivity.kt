package io.github.tormundsmember.easyflashcards.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zhuinden.simplestack.*
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.BackstackHandler
import io.github.tormundsmember.easyflashcards.ui.base_ui.FragmentStateChanger
import io.github.tormundsmember.easyflashcards.ui.set_overview.SetOverviewKey

/**
 * design available in https://projects.invisionapp.com/freehand/document/Qjl32e0Ze
 */
class MainActivity : AppCompatActivity(), StateChanger, BackstackHandler {

    private lateinit var fragmentStateChanger: FragmentStateChanger
    override val backstack: Backstack by lazy { backstackDelegate.backstack }
    private lateinit var backstackDelegate: BackstackDelegate

    override fun onCreate(savedInstanceState: Bundle?) {

        Dependencies.init(this)

        backstackDelegate = BackstackDelegate()
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
}
