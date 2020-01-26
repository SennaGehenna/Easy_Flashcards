package io.github.tormundsmember.easyflashcards.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserData(private val context: Context) {

    companion object {
        const val SHARED_PREF_NAME = "easy_flashcards"
        const val HAS_SEEN_TUTORIAL = "HAS_SEEN_TUTORIAL"
        const val HAS_SEEN_OVERVIEW_TUTORIAL = "HAS_SEEN_OVERVIEW_TUTORIAL"
        const val USE_DARKMODE = "USE_DARKMODE"
        const val USE_SPACED_REPETITION = "USE_SPACED_REPETITION "
        const val ALLOW_CRASHREPORTING = "ALLOW_CRASHREPORTING"
    }

    private val sharedPref: SharedPreferences
        get() = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    var hasSeenSetsTutorial: Boolean
        get() = sharedPref.getBoolean(HAS_SEEN_TUTORIAL, false)
        set(value) {
            sharedPref.edit {
                putBoolean(HAS_SEEN_TUTORIAL, value)
            }
        }

    var hasSeenSetOverviewTutorial: Boolean
        get() = sharedPref.getBoolean(HAS_SEEN_OVERVIEW_TUTORIAL, false)
        set(value) {
            sharedPref.edit {
                putBoolean(HAS_SEEN_OVERVIEW_TUTORIAL, value)
            }
        }

    var useDarkMode: Boolean
        get() = sharedPref.getBoolean(USE_DARKMODE, false)
        set(value) {
            sharedPref.edit {
                putBoolean(USE_DARKMODE, value)
            }
        }

    var useSpacedRepetition: Boolean
        get() = sharedPref.getBoolean(USE_SPACED_REPETITION, false)
        set(value) {
            sharedPref.edit {
                putBoolean(USE_SPACED_REPETITION, value)
            }
        }

    var allowCrashReporting: Boolean
        get() = sharedPref.getBoolean(ALLOW_CRASHREPORTING, false)
        set(value) {
            sharedPref.edit {
                putBoolean(ALLOW_CRASHREPORTING, value)
            }
        }
}