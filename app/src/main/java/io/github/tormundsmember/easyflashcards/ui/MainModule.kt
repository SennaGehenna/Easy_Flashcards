package io.github.tormundsmember.easyflashcards.ui

import io.github.tormundsmember.easyflashcards.data.Database
import io.github.tormundsmember.easyflashcards.data.UserData

interface MainModule {

    val database: Database
    val userData: UserData

}