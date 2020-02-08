package io.github.tormundsmember.easyflashcards.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.zhuinden.simplestack.*
import com.zhuinden.simplestack.navigator.Navigator
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.BackstackHandler
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import io.github.tormundsmember.easyflashcards.ui.base_ui.FragmentStateChanger
import io.github.tormundsmember.easyflashcards.ui.set_overview.SetOverviewKey

/**
 * design available in https://projects.invisionapp.com/freehand/document/Qjl32e0Ze
 */
class MainActivity : AppCompatActivity(), BackstackHandler, SimpleStateChanger.NavigationHandler {

    private lateinit var fragmentStateChanger: FragmentStateChanger
    override lateinit var backstack: Backstack

    override fun onCreate(savedInstanceState: Bundle?) {

        Dependencies.init(this)

        setDarkMode()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.fragmentContainer)

        backstack = Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, findViewById(R.id.fragmentContainer), History.single(SetOverviewKey()))
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }

    override fun onBackPressed() {
        if (!backstack.goBack()) {
            val fragmentTag = backstack.top<BaseKey>().fragmentTag
            val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
            if (fragment != null && fragment is BaseFragment) {
                fragment.handleBackPress()
            } else {
                goBack()
            }
        }
    }

    fun goBack() {
        super.onBackPressed()
    }

    fun setDarkMode() {
        if (Dependencies.userData.useDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
