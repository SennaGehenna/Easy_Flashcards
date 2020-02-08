package io.github.tormundsmember.easyflashcards.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration2_3 : Migration(2,3) {

    override fun migrate(database: SupportSQLiteDatabase) {


        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_Card_backText ON Card (backText);
        """.trimIndent())
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_Card_frontText ON Card (frontText);
        """.trimIndent())


    }
}