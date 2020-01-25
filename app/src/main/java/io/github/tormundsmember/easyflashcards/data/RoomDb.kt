package io.github.tormundsmember.easyflashcards.data

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set

@Database(
    entities = [
        Set::class,
        Card::class
    ],
    version = 1
)
abstract class RoomDb : RoomDatabase() {

    abstract fun getDao() : io.github.tormundsmember.easyflashcards.data.Database
}