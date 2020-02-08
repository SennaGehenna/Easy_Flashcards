package io.github.tormundsmember.easyflashcards.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """CREATE TABLE IF NOT EXISTS `CardsTemp` (
            `id` INTEGER NOT NULL, 
            `frontText` TEXT NOT NULL, 
            `backText` TEXT NOT NULL, 
            `currentInterval` TEXT NOT NULL, 
            `nextRecheck` INTEGER NOT NULL, 
            `setId` INTEGER NOT NULL, 
            `checkCount` INTEGER NOT NULL, 
            `positiveCheckCount` INTEGER NOT NULL, 
            PRIMARY KEY(`id`), 
            FOREIGN KEY(`setId`) REFERENCES `Set`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE );
        """
        )
        database.execSQL(
            """insert into CardsTemp select 
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
            from Card;"""
        )
        database.execSQL("Drop Table Card;")
        database.execSQL("Alter Table CardsTemp rename to Card;")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_Card_setId` ON `Card` (`setId`)")


    }


}