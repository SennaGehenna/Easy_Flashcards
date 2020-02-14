package io.github.tormundsmember.easyflashcards.ui.util

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class sharedPref(private val sharedPref: SharedPreferences, val key: String) : ReadWriteProperty<Any?, Boolean> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        sharedPref.edit {
            putBoolean(key, value)
        }
    }

}