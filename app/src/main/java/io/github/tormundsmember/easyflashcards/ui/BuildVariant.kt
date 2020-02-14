package io.github.tormundsmember.easyflashcards.ui

import io.github.tormundsmember.easyflashcards.BuildConfig

object BuildVariant{

    fun isProductionBuild(): Boolean {
        return BuildConfig.isProductionBuild
    }

}