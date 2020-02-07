package io.github.tormundsmember.easyflashcards.data

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.tormundsmember.easyflashcards.ui.more.model.CardWithSetData
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set

@Dao
interface Database {

    @Query("select coalesce(max(id),0)+1 from `set`")
    fun getHighestSetId(): Int

    @Query("select coalesce(max(id),0)+1 from card where setId = :setId")
    fun getHighestCardIdForSet(setId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSet(set: Set)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSets(set: List<Set>)

    @Update
    fun updateSet(set:Set)

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

    @Query("select * from card where setId in (:ids) and card.nextRecheck <= :currentTime")
    fun getCardsByMultipleSetIdsWithSpacedRepetion(ids: List<Int>, currentTime: Long = System.currentTimeMillis()): List<Card>

    @Query("select * from `set` where id = :id")
    fun getSetById(id: Int): Set

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateCards(card: List<Card>)

    @Delete
    fun deleteSet(set: Set)

    @Delete
    fun deleteCard(card: Card)

}