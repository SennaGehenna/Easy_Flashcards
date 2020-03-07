package io.github.tormundsmember.easyflashcards.ui

import android.app.Application
import androidx.room.Room
import io.github.tormundsmember.easyflashcards.BuildConfig
import io.github.tormundsmember.easyflashcards.data.Database
import io.github.tormundsmember.easyflashcards.data.RoomDb
import io.github.tormundsmember.easyflashcards.data.UserData
import io.github.tormundsmember.easyflashcards.data.UserDataImpl
import io.github.tormundsmember.easyflashcards.ui.util.factory

class App : Application() {


    override fun onCreate() {
        super.onCreate()

        Dependencies.init(object : MainModule {
            private val roomDb: RoomDb by lazy {
                Room.databaseBuilder(applicationContext, RoomDb::class.java, BuildConfig.db_name)
                    .addMigrations(
                        *RoomDb.getMigrations()
                    )
                    .allowMainThreadQueries()
                    .build()

            }
            override val database: Database by factory { roomDb.getDao() }
            override val userData: UserData by factory { UserDataImpl(applicationContext) }
        })
    }
}