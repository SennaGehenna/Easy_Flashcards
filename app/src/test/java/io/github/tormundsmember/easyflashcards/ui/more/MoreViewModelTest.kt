package io.github.tormundsmember.easyflashcards.ui.more

import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.InputStream


class MoreViewModelTest {

    @Test
    fun `should get two cards and one set`() {

        val input: InputStream = listOf(
            "setId;setName;cardId;frontText;backText;currentInterval;nextRecheck;checkCount;positiveCheckCount",
            "1;japanese;1;grandfather;ojiisan;1;1;1;1",
            "1;japanese;2;grandmother;obaasan;1;1;1;1"
        ).joinToString(separator = System.lineSeparator()).byteInputStream()



        val (sets, cards) = runBlocking { MoreViewModel().obtainImportedData(input) }


        assertTrue(sets.size == 1)
        assertTrue(cards.size == 2)
        assertTrue(Set(1, "japanese") == sets[0])
        assertTrue(
            Card(1, "grandfather", "ojiisan", 1, 1, 1, 1, 1) == cards[0]
        )
        assertTrue(
            Card(2, "grandmother", "obaasan", 1, 1, 1, 1, 1) == cards[1]
        )
    }

}