package io.github.tormundsmember.easyflashcards.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set

@Database(
    entities = [
        Set::class,
        Card::class
    ],
    version = 3
)
@TypeConverters(
    RehearsalInterval.TypeConverter::class
)
abstract class RoomDb : RoomDatabase() {

    abstract fun getDao(): io.github.tormundsmember.easyflashcards.data.Database
}