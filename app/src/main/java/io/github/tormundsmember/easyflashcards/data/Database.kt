package io.github.tormundsmember.easyflashcards.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.tormundsmember.easyflashcards.ui.more.model.CardWithSetData
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import java.util.concurrent.TimeUnit

@Dao
interface Database {

    @Query("select max(id) from `set`")
    fun getHighestSetId(): Int

    @Query("select max(id) from card where setId = :setId")
    fun getHighestCardIdForSet(setId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateSet(set: Set)

    @Query("select * from `set`")
    fun getSets(): List<Set>

    @Query("select s.id as setId,s.name,c.id as cardId, c.frontText, c.backText, c.currentInterval, c.nextRecheck, c.checkCount, c.positiveCheckCount from card c join `set` s on c.setId = s.id")
    fun getCardsWithSetNames(): List<CardWithSetData>

    @Query("select * from `set`")
    fun observeSets(): LiveData<List<Set>>

    @Query("select * from card where setId = :setId")
    fun observeSet(setId: Int): LiveData<List<Card>>

    @Query("select * from card where setId = :id")
    fun getCardsBySetId(id: Int): List<Card>

    @Query("select * from card where setId in (:ids)")
    fun getCardsByMultipleSetIds(ids: List<Int>): List<Card>

    @Query("select * from `set` where id = :id")
    fun getSetById(id: Int): Set

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateCard(card: Card)

}