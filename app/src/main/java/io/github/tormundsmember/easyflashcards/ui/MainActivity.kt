package io.github.tormundsmember.easyflashcards.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatTextView
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.SimpleStateChanger
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.navigator.Navigator
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseKey
import io.github.tormundsmember.easyflashcards.ui.base_ui.FragmentStateChanger
import io.github.tormundsmember.easyflashcards.ui.base_ui.MainScreen
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.SetOverviewKey
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.util.*
import java.util.concurrent.TimeUnit

/**
 * design available in https://projects.invisionapp.com/freehand/document/Qjl32e0Ze
 */
class MainActivity : AppCompatActivity(), MainScreen, SimpleStateChanger.NavigationHandler {

    private lateinit var fragmentStateChanger: FragmentStateChanger
    override lateinit var backstack: Backstack

    private lateinit var fullSpinner: View

    override fun onCreate(savedInstanceState: Bundle?) {

        setDarkMode()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.fragmentContainer)
        backstack = Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, findViewById(R.id.fragmentContainer), History.single(SetOverviewKey()))

        fullSpinner = findViewById(R.id.fullSpinner)
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
        with(Dependencies.userData) {
            if (hasOldDarkModeSetting) {
                currentDarkModeSetting = if (useDarkMode) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                removeOldDarkModeSetting()
            }
            if (!listOf(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                    AppCompatDelegate.MODE_NIGHT_YES,
                    AppCompatDelegate.MODE_NIGHT_NO
                ).contains(currentDarkModeSetting)
            ) {
                currentDarkModeSetting = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            AppCompatDelegate.setDefaultNightMode(currentDarkModeSetting)
        }
    }

    override fun showSetsTutorial(onAddButtonClick: Action, onCancel: Action) {
        with(findViewById<View>(R.id.vTutorial)) {
            setOnClickListener {
                hideCurrentTutorial(onAddButtonClick = {}, onCancel = onCancel)
            }
            alpha = 0F
            visible()
            this.animate()
                .setDuration(600)
                .alpha(0.8F)
                .resetListener()
                .start()
        }
        with(findViewById<View>(R.id.imgTutorialMenuAdd)) {
            alpha = 0F
            visible()
            this.animate()
                .setStartDelay(1300)
                .setDuration(300)
                .alpha(0.8F)
                .resetListener()
                .start()
            setOnClickListener {
                setOnClickListener(null)
                hideCurrentTutorial(onAddButtonClick, {})
            }
        }
        with(findViewById<View>(R.id.txtTutorialSets)) {
            alpha = 0F
            visible()
            this.animate()
                .setStartDelay(1300)
                .setDuration(300)
                .alpha(0.8F)
                .resetListener()
                .start()
        }
    }

    override fun showCardsTutorial(
        setName: String,
        onAddButtonClick: Action,
        onCancel: Action
    ) {
        with(findViewById<View>(R.id.vTutorial)) {
            setOnClickListener {
                hideCurrentTutorial(onAddButtonClick = {}, onCancel = onCancel)
            }
            alpha = 0F
            visible()
            this.animate()
                .setDuration(600)
                .alpha(0.8F)
                .resetListener()
                .start()
        }
        with(findViewById<View>(R.id.imgTutorialMenuAdd)) {
            alpha = 0F
            visible()
            this.animate()
                .setStartDelay(1300)
                .setDuration(300)
                .alpha(0.8F)
                .resetListener()
                .start()
            setOnClickListener {
                setOnClickListener(null)
                hideCurrentTutorial(onAddButtonClick, {})
            }
        }
        with(findViewById<AppCompatTextView>(R.id.txtTutorialCards)) {
            text = getString(R.string.no_cards_tutorial, setName)
            alpha = 0F
            visible()
            this.animate()
                .setStartDelay(1300)
                .setDuration(300)
                .alpha(0.8F)
                .resetListener()
                .start()
        }
    }

    override fun hideCurrentTutorial(onAddButtonClick: Action, onCancel: Action) {
        listOf(
            R.id.vTutorial,
            R.id.imgTutorialMenuAdd,
            R.id.txtTutorialSets,
            R.id.txtTutorialCards
        ).map {
            findViewById<View>(it)
        }.forEach {
            it.animate()
                .setStartDelay(0)
                .setDuration(300)
                .alpha(0F)
                .setListener(
                    onAnimationEnd = {
                        it.gone()
                        if (it.id == R.id.imgTutorialMenuAdd) {
                            onAddButtonClick()
                            onCancel()
                        }
                    })
        }
    }

    override fun showFullProgressBar() {
        fullSpinner.setOnClickListener {
            //intentionally left blank, this should block the user completely
        }
        fullSpinner.visible()
    }

    override fun hideFullProgressBar() {
        fullSpinner.setOnClickListener(null)
        fullSpinner.gone()
    }
}
