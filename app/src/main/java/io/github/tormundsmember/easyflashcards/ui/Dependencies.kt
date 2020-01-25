package io.github.tormundsmember.easyflashcards.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import io.github.tormundsmember.easyflashcards.data.Database
import io.github.tormundsmember.easyflashcards.data.RoomDb
import io.github.tormundsmember.easyflashcards.data.UserData
import io.github.tormundsmember.easyflashcards.ui.more.model.CardWithSetData
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.util.plusAssign

@SuppressLint("StaticFieldLeak") //this friggin DI, if we leak this context, we're doing something seriously wrong
object Dependencies {

    lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    val inMemoryDatabase: Database by lazy {

        object : Database {
            override fun getCardsWithSetNames(): List<CardWithSetData> {
                return cardsInDb.map { card ->
                    setsInDb.first { set -> set.id == card.setId }.let {
                        CardWithSetData(
                            it.id, it.name, card.id,
                            card.frontText,
                            card.backText,
                            card.currentInterval,
                            card.nextRecheck,
                            card.checkCount,
                            card.positiveCheckCount
                        )
                    }

                }
            }

            val setsInDb: MutableList<Set> = mutableListOf(
                Set(1, "Lesson 1")
            )
            val cardsInDb: MutableList<Card> = mutableListOf(
                Card(
                    1,
                    "Ojiisan",
                    "Grandfather",
                    1,
                    System.currentTimeMillis() - 1000,
                    1,
                    0,
                    0
                ),
                Card(
                    2,
                    "Obaasan",
                    "Grandmother",
                    1,
                    System.currentTimeMillis() - 1000,
                    1,
                    0,
                    0
                )
            )

            val cardsLiveData = MutableLiveData<List<Card>>().apply { this += cardsInDb }
            val setsLiveData = MutableLiveData<List<Set>>().apply { this += setsInDb }

            override fun observeSets(): LiveData<List<Set>> {
                return setsLiveData
            }

            override fun observeSet(setId: Int): LiveData<List<Card>> {
                val mediatorLiveData = MediatorLiveData<List<Card>>()
                return mediatorLiveData.apply {
                    addSource(cardsLiveData) {
                        mediatorLiveData += it.filter { card ->
                            card.setId == setId
                        }
                    }
                }
            }

            override fun getCardsByMultipleSetIds(ids: List<Int>): List<Card> {
                return cardsInDb.filter { it.setId in ids }.sortedBy { it.id }
            }

            override fun addOrUpdateSet(set: Set) {
                setsInDb.removeAll { it.id == set.id }
                setsInDb.add(set)
                setsLiveData += setsInDb.sortedBy { it.id }
            }

            override fun getHighestCardIdForSet(setId: Int): Int {
                return (cardsInDb.filter { it.setId == setId }.maxBy { it.id }?.id ?: 0) + 1
            }

            override fun getSets(): List<Set> {
                return setsInDb.sortedBy { it.name }
            }

            override fun getCardsBySetId(id: Int): List<Card> {
                return cardsInDb.filter { it.setId == id }.sortedBy { it.id }
            }

            override fun getSetById(id: Int): Set {
                return setsInDb.first { it.id == id }
            }

            override fun addOrUpdateCard(card: Card) {
                cardsInDb.removeAll { it.id == card.id }
                cardsInDb.add(card)
                cardsLiveData += cardsInDb.sortedBy { it.id }
            }

            override fun getHighestSetId(): Int {
                return (setsInDb.maxBy { it.id }?.id ?: 0) + 1
            }
        }

    }

    private val roomDb: RoomDb by lazy {
        Room.databaseBuilder(context, RoomDb::class.java, "flashcards_db")
            .allowMainThreadQueries()
            .build()

    }

    val database: Database
        get() = roomDb.getDao()

    val userData: UserData by lazy {
        UserData()
    }
}