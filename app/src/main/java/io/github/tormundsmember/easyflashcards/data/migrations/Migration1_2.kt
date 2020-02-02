package io.github.tormundsmember.easyflashcards.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration1_2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            
            CREATE TABLE CardsTemp (
                `id` INTEGER NOT NULL, 
                `frontText` TEXT NOT NULL, 
                `backText` TEXT NOT NULL, 
                `currentInterval` TEXT NOT NULL, 
                `nextRecheck` INTEGER NOT NULL, 
                `setId` INTEGER NOT NULL, 
                `checkCount` INTEGER NOT NULL, 
                `positiveCheckCount` INTEGER NOT NULL 
            );
            insert into CardsTemp select 
                id, 
                frontText, 
                backText, 
                case currentInterval
                    when 1 then "STAGE_1"
                    when 3 then "STAGE_2"
                    when 7 then "STAGE_3"
                    when 14 then "STAGE_4"
                    when 20 then "STAGE_5"
                    else "DONE"
                end,
                nextRecheck,
                setId,
                checkCount,
                positiveCheckCount
            from Card;
            
            Drop Table Card;
            
            Alter Table CardsTemp rename to Card;
        """.trimIndent()
        )


    }


}