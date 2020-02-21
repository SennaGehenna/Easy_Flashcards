package io.github.tormundsmember.easyflashcards.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.tormundsmember.easyflashcards.data.migrations.Migration1_2
import io.github.tormundsmember.easyflashcards.data.migrations.Migration2_3
import io.github.tormundsmember.easyflashcards.data.migrations.Migration3_4
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set

@Database(
    entities = [
        Set::class,
        Card::class
    ],
    version = 4
)
@TypeConverters(
    RehearsalInterval.TypeConverter::class
)
abstract class RoomDb : RoomDatabase() {

    abstract fun getDao(): io.github.tormundsmember.easyflashcards.data.Database

    companion object {

        fun getMigrations() = arrayOf(
            Migration1_2,
            Migration2_3,
            Migration3_4
        )

    }
}