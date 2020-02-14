package io.github.tormundsmember.easyflashcards.ui

import io.github.tormundsmember.easyflashcards.data.Database
import io.github.tormundsmember.easyflashcards.data.UserData

object Dependencies {

    private lateinit var mainModule: MainModule

    fun init(mainModule: MainModule) {
        this.mainModule = mainModule
    }


    val database: Database
        get() = mainModule.database
    val userData: UserData
        get() = mainModule.userData


}


