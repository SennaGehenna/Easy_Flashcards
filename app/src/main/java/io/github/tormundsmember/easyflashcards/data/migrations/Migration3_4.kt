package io.github.tormundsmember.easyflashcards.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval

val Migration3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {

        val midnightToday = RehearsalInterval.getNextRehearsalDate(0)

        database.execSQL("""
            update Card set 
                nextRecheck = $midnightToday, 
                currentinterval = "STAGE_5" 
            where 
                currentInterval in("STAGE_5","DONE");
            """)



    }

}