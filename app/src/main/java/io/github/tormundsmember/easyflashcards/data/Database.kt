package io.github.tormundsmember.easyflashcards.data

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.tormundsmember.easyflashcards.ui.more.model.CardWithSetData
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import java.util.*

@Dao
abstract class Database {

    @Query("select coalesce(max(id),0)+1 from `set`")
    abstract fun getHighestSetId(): Int

    @Query("select coalesce(max(id),0)+1 from card")
    abstract fun getHighestCardIdForSet(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addSet(set: Set)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addSets(set: List<Set>)

    @Update
    abstract fun updateSet(set: Set)

    @Query("select * from `set`")
    abstract fun getSets(): List<Set>

    @Query("select s.id as setId,s.name,c.id as cardId, c.frontText, c.backText, c.currentInterval, c.nextRecheck, c.checkCount, c.positiveCheckCount from card c join `set` s on c.setId = s.id")
    abstract fun getCardsWithSetNames(): List<CardWithSetData>

    @Query("select * from `set`")
    abstract fun observeSets(): LiveData<List<Set>>

    @Query("select * from card where setId = :setId")
    abstract fun observeSet(setId: Int): LiveData<List<Card>>

    @Query("select * from card where id = :id")
    abstract fun getCardById(id: Int): Card

    @Query("select * from card where setId = :id")
    abstract fun getCardsBySetId(id: Int): List<Card>

    @Query("select * from card where setId in (:ids)")
    abstract fun getCardsByMultipleSetIds(ids: List<Int>): List<Card>

    @Query("select * from card where setId in (:ids) and card.nextRecheck <= :currentTime")
    abstract fun getCardsByMultipleSetIdsWithSpacedRepetion(
        ids: List<Int>,
        currentTime: Long = System.currentTimeMillis()
    ): List<Card>

    @Query("select * from `set` where id = :id")
    abstract fun getSetById(id: Int): Set

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addOrUpdateCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun addOrUpdateCards(card: List<Card>)

    @Delete
    abstract fun deleteSet(set: Set)

    @Delete
    abstract fun deleteCard(card: Card)

    @Query("UPDATE Card SET nextRecheck = 0, currentInterval = 'STAGE_1' WHERE id in (:ids)")
    abstract fun resetProgressFor(ids: List<Int>)


    @Query(
        """select * from Card where 
  frontText in (select frontText from Card group by frontText having count(*) != 1) or
  backText in (select backText from Card group by frontText having count(*) != 1)"""
    )
    abstract fun getDuplicates(): List<Card>


    @Query(
        """
        select * from Card where
            lower(frontText) like :term or
             lower(backText) like :term
    """
    )
    protected abstract fun getCardsByPartialName(term: String): List<Card>

    fun getCardsBySearchTerm(term: String) = getCardsByPartialName("%$term%".toLowerCase(Locale.getDefault()))
}