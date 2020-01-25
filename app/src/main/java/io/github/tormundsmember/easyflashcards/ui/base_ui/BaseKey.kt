package io.github.tormundsmember.easyflashcards.ui.base_ui

import android.os.Bundle
import android.os.Parcelable

abstract class BaseKey : Parcelable {
    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment {
        val fragment = createFragment()
        var bundle = fragment.arguments
        if (bundle == null) {
            bundle = Bundle()
        }
        bundle.putParcelable(KEY, this)
        fragment.arguments = bundle
        return fragment
    }

    protected abstract fun createFragment(): BaseFragment

    companion object {
        const val KEY = "KEY"
    }
}