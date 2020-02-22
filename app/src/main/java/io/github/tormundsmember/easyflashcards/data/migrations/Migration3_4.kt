package io.github.tormundsmember.easyflashcards.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.tormundsmember.easyflashcards.ui.util.getStartOfDay

val Migration3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {

        val midnightToday = getStartOfDay()

        database.execSQL("""
            update Card set 
                nextRecheck = $midnightToday, 
                currentinterval = "STAGE_5" 
            where 
                currentInterval in("STAGE_5","DONE");
            """)



    }

}