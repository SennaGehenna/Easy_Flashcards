package io.github.tormundsmember.easyflashcards.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UserData(private val context: Context) {

    companion object {
        const val SHARED_PREF_NAME = "easy_flashcards"
        const val HAS_SEEN_TUTORIAL = "HAS_SEEN_TUTORIAL"
        const val HAS_SEEN_TUTORIAL_WITH_EXISTING_ITEMS = "HAS_SEEN_TUTORIAL_WITH_EXISTING_ITEMS"
        const val HAS_SEEN_OVERVIEW_TUTORIAL = "HAS_SEEN_OVERVIEW_TUTORIAL"
        const val HAS_SEEN_OVERVIEW_TUTORIAL_WITH_EXISTING_ITEMS = "HAS_SEEN_OVERVIEW_TUTORIAL_WITH_EXISTING_ITEMS"
        const val USE_DARKMODE = "USE_DARKMODE"
        const val USE_SPACED_REPETITION = "USE_SPACED_REPETITION "
        const val ALLOW_CRASHREPORTING = "ALLOW_CRASHREPORTING"
        const val LIMIT_CARDS = "LIMIT_CARDS"
        const val LIMIT_CARDS_AMOUNT = "LIMIT_CARDS_AMOUNT"
    }

    private val sharedPrefs: SharedPreferences
        get() = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)


    var hasSeenSetsTutorial: Boolean by sharedPrefBoolean(HAS_SEEN_TUTORIAL)

    var hasSeenSetsTutorialWithExistingItems: Boolean by sharedPrefBoolean(HAS_SEEN_TUTORIAL_WITH_EXISTING_ITEMS)

    var hasSeenSetOverviewTutorial: Boolean by sharedPrefBoolean(HAS_SEEN_OVERVIEW_TUTORIAL)

    var hasSeenSetOverviewTutorialWithExistingItems: Boolean by sharedPrefBoolean(HAS_SEEN_OVERVIEW_TUTORIAL_WITH_EXISTING_ITEMS)

    var useDarkMode: Boolean by sharedPrefBoolean(USE_DARKMODE)

    var useSpacedRepetition: Boolean by sharedPrefBoolean(USE_SPACED_REPETITION)

    var allowCrashReporting: Boolean by sharedPrefBoolean(ALLOW_CRASHREPORTING)

    var limitCards: Boolean by sharedPrefBoolean(LIMIT_CARDS)

    var limitCardsAmount: Int by sharedPrefInt(LIMIT_CARDS_AMOUNT)


    private inner class sharedPrefBoolean(val key: String, val defaultValue: Boolean = false) : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return sharedPrefs.getBoolean(key, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            sharedPrefs.edit {
                putBoolean(key, value)
            }
        }

    }

    private inner class sharedPrefInt(val key: String) : ReadWriteProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return sharedPrefs.getInt(key, -1)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            sharedPrefs.edit {
                putInt(key, value)
            }
        }

    }

}

