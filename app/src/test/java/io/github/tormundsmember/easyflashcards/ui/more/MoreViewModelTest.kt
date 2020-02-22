package io.github.tormundsmember.easyflashcards.ui.more

import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.util.getStartOfDay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class MoreViewModelTest {

    private val firstCard = Card(
        id = 1,
        frontText = "grandfather",
        backText = "ojiisan",
        currentInterval = RehearsalInterval.STAGE_1,
        nextRecheck = getStartOfDay(),
        setId = 1,
        checkCount = 0,
        positiveCheckCount = 0
    )
    private val secondCard = Card(
        id = 2,
        frontText = "grandmother",
        backText = "obaasan",
        currentInterval = RehearsalInterval.STAGE_1,
        nextRecheck = getStartOfDay(),
        setId = 1,
        checkCount = 0,
        positiveCheckCount = 0
    )

    @Test
    fun `should get two cards and one set`() {
        val expectedSetId = 1
        val expectedSetName = "japanese"

        fun Card.getTestData() =
            "$expectedSetId;$expectedSetName;$id;$frontText;$backText;$currentInterval;$nextRecheck;$checkCount;$positiveCheckCount"

        val input = listOf(
            obtainFullHeader(),
            firstCard.getTestData(),
            secondCard.getTestData()
        ).joinToString(separator = System.lineSeparator())



        val (sets, cards) = runBlocking { MoreViewModel().obtainImportedData(input.byteInputStream()) }


        assertTrue(sets.size == 1)
        assertTrue(cards.size == 2)
        assertEquals(Set(expectedSetId, expectedSetName), sets[0])
        assertEquals(firstCard, cards[0])
        assertEquals(secondCard, cards[1])
    }

    @Test
    fun `should import two cards and one set with absolute minimum of supplied keys`() {
        val expectedSetId = 1
        val expectedSetName = "import_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}"
        val input: InputStream = listOf(
            obtainHeader(),
            "grandfather;ojiisan",
            "grandmother;obaasan"
        ).joinToString(separator = System.lineSeparator()).byteInputStream()

        val (sets, cards) = runBlocking { MoreViewModel().obtainImportedData(input) }


        assertTrue(sets.size == 1)
        assertTrue(cards.size == 2)
        assertTrue(Set(expectedSetId, expectedSetName) == sets[0])
        assertEquals(firstCard, cards[0])
        assertEquals(secondCard, cards[1])

    }

    @Test
    fun `should import two cards and one set with absolute minimum of supplied keys and setname`() {
        val expectedSetId = 1
        val expectedSetName = "import_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}"
        fun Card.getTestData() = "$expectedSetName;$frontText;$backText"

        val input: InputStream = listOf(
            obtainHeader(showSetName = true),
            firstCard.getTestData(),
            secondCard.getTestData()
        ).joinToString(separator = System.lineSeparator()).byteInputStream()

        val (sets, cards) = runBlocking { MoreViewModel().obtainImportedData(input) }


        assertTrue(sets.size == 1)
        assertTrue(sets.size == 1)
        assertTrue(cards.size == 2)
        assertTrue(Set(expectedSetId, expectedSetName) == sets[0])
        assertEquals(firstCard, cards[0])
        assertEquals(secondCard, cards[1])


    }


    companion object {

        fun obtainFullHeader() = obtainHeader(
            showSetId = true,
            showSetName = true,
            showCardId = true,
            showCurrentInterval = true,
            showNextRecheck = true,
            showCheckCount = true,
            showPositiveCheckCount = true
        )

        fun obtainHeader(
            showSetId: Boolean = false,
            showSetName: Boolean = false,
            showCardId: Boolean = false,
            showCurrentInterval: Boolean = false,
            showNextRecheck: Boolean = false,
            showCheckCount: Boolean = false,
            showPositiveCheckCount: Boolean = false
        ) : String {

            val resultList = mutableListOf<String>()

            if(showSetId){
                resultList += "setId"
            }
            if(showSetName){
                resultList += "setName"
            }
            if(showCardId){
                resultList += "cardId"
            }
            resultList+="frontText"
            resultList+="backText"
            if(showCurrentInterval){
                resultList += "currentInterval"
            }
            if(showNextRecheck){
                resultList += "nextRecheck"
            }
            if(showCheckCount){
                resultList += "checkCount"
            }
            if(showPositiveCheckCount){
                resultList += "positiveCheckCount"
            }
            return resultList.joinToString(";")
        }

    }
}