package io.github.tormundsmember.easyflashcards.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UserDataImpl(private val context: Context) : UserData {

    companion object {
        const val SHARED_PREF_NAME = "easy_flashcards"
        const val HAS_SEEN_TUTORIAL = "HAS_SEEN_TUTORIAL"
        const val HAS_SEEN_TUTORIAL_WITH_EXISTING_ITEMS = "HAS_SEEN_TUTORIAL_WITH_EXISTING_ITEMS"
        const val HAS_SEEN_OVERVIEW_TUTORIAL = "HAS_SEEN_OVERVIEW_TUTORIAL"
        const val HAS_SEEN_OVERVIEW_TUTORIAL_WITH_EXISTING_ITEMS = "HAS_SEEN_OVERVIEW_TUTORIAL_WITH_EXISTING_ITEMS"
        const val USE_DARKMODE = "USE_DARKMODE"
        const val USE_SPACED_REPETITION = "USE_SPACED_REPETITION"
        const val DO_NOT_SHOW_LEARNED_CARDS = "DO_NOT_SHOW_LEARNED_CARDS"
        const val ALLOW_CRASHREPORTING = "ALLOW_CRASHREPORTING"
        const val LIMIT_CARDS = "LIMIT_CARDS"
        const val LIMIT_CARDS_AMOUNT = "LIMIT_CARDS_AMOUNT"
    }

    private val sharedPrefs: SharedPreferences
        get() = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)


    override var hasSeenSetsTutorial: Boolean by SharedPrefBoolean(HAS_SEEN_TUTORIAL)

    override var hasSeenSetsTutorialWithExistingItems: Boolean by SharedPrefBoolean(HAS_SEEN_TUTORIAL_WITH_EXISTING_ITEMS)

    override var hasSeenSetOverviewTutorial: Boolean by SharedPrefBoolean(HAS_SEEN_OVERVIEW_TUTORIAL)

    override var hasSeenSetOverviewTutorialWithExistingItems: Boolean by SharedPrefBoolean(HAS_SEEN_OVERVIEW_TUTORIAL_WITH_EXISTING_ITEMS)

    override var useDarkMode: Boolean by SharedPrefBoolean(USE_DARKMODE)

    override var useSpacedRepetition: Boolean by SharedPrefBoolean(USE_SPACED_REPETITION)

    override var allowCrashReporting: Boolean by SharedPrefBoolean(ALLOW_CRASHREPORTING)

    override var limitCards: Boolean by SharedPrefBoolean(LIMIT_CARDS)

    override var limitCardsAmount: Int by SharedPrefInt(LIMIT_CARDS_AMOUNT)

    override var doNotShowLearnedCards: Boolean by SharedPrefBoolean(DO_NOT_SHOW_LEARNED_CARDS)

    private inner class SharedPrefBoolean(val key: String, val defaultValue: Boolean = false) : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return sharedPrefs.getBoolean(key, defaultValue)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            sharedPrefs.edit {
                putBoolean(key, value)
            }
        }

    }

    private inner class SharedPrefInt(val key: String) : ReadWriteProperty<Any?, Int> {
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

