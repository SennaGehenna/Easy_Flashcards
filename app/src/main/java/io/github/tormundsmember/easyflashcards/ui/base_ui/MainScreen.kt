package io.github.tormundsmember.easyflashcards.ui.base_ui

import com.zhuinden.simplestack.Backstack
import io.github.tormundsmember.easyflashcards.ui.util.Action

interface MainScreen {

    val backstack: Backstack

    fun showSetsTutorial(onAddButtonClick: Action)

    fun showCardsTutorial(setName: String, onAddButtonClick: Action)

    fun hideCurrentTutorial(onAddButtonClick: Action)

}