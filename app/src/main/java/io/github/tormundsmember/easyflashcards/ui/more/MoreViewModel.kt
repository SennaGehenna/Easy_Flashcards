package io.github.tormundsmember.easyflashcards.ui.more

import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

class MoreViewModel : BaseViewModel() {


    suspend fun importFromCsv(inputStream: InputStream) = withContext(Dispatchers.IO) {
        val (sets, cards) = obtainImportedData(inputStream)
        with(Dependencies.database) {
            sets.forEach { updateSet(it) }
            cards.forEach { addOrUpdateCard(it) }
        }
    }

    fun obtainImportedData(inputStream: InputStream): Pair<List<Set>, List<Card>> {
        fun List<String>.getOrNullWithNullableKey(key: Int?) = if (key != null) this.getOrNull(key) else null

        var csv: List<String> = emptyList()
        BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use {
            csv = it.readLines()
        }


        val setIdKey = "setId"
        val setNameKey = "setName"
        val cardIdKey = "cardId"
        val frontTextKey = "frontText"
        val backTextKey = "backText"
        val currentIntervalKey = "currentInterval"
        val nextRecheckKey = "nextRecheck"
        val checkCountKey = "checkCount"
        val positiveCheckCountKey = "positiveCheckCount"
        val requiredColumns = listOf(
            setIdKey,
            setNameKey,
            cardIdKey,
            frontTextKey,
            backTextKey
            ,
            currentIntervalKey,
            nextRecheckKey,
            checkCountKey,
            positiveCheckCountKey
        )


        val columnNames = csv.first().split(";").filter { it.isNotEmpty() }.withIndex().associate {
            Pair(it.value, it.index)
        }

        val sets: MutableList<Set> = mutableListOf()
        val cards: MutableList<Card> = mutableListOf()

        val setId = columnNames[setIdKey]
        val setName = columnNames[setNameKey]
        val cardId = columnNames[cardIdKey]
        val frontText = columnNames[frontTextKey]
        val backText = columnNames[backTextKey]
        val currentInterval = columnNames[currentIntervalKey]
        val nextRecheck = columnNames[nextRecheckKey]
        val checkCount = columnNames[checkCountKey]
        val positiveCheckCount = columnNames[positiveCheckCountKey]

        try {
            if (columnNames.keys.containsAll(requiredColumns)) {
                csv.drop(1).map { it.split(";") }.forEach { row: List<String> ->
                    if (sets.none { it.id == row[setId!!].toInt() }) {
                        val set = Set(row[setId!!].toInt(), row[setName!!])
                        sets += set
                    }
                    val _id = row.getOrNullWithNullableKey(cardId)?.toInt()!!
                    val _frontText = row.getOrNullWithNullableKey(frontText)!!
                    val _backText = row.getOrNullWithNullableKey(backText)!!
                    val _currentInterval =
                        RehearsalInterval.TypeConverter.fromString(row.getOrNullWithNullableKey(currentInterval)!!)
                    val _nextRecheck = row.getOrNullWithNullableKey(nextRecheck)?.toLong()!!
                    val _setId = row.getOrNullWithNullableKey(setId)?.toInt()!!
                    val _checkCount = row.getOrNullWithNullableKey(checkCount)?.toInt()!!
                    val _positiveCheckCount = row.getOrNullWithNullableKey(positiveCheckCount)?.toInt()!!
                    cards += Card(
                        id = _id,
                        frontText = _frontText,
                        backText = _backText,
                        currentInterval = _currentInterval,
                        nextRecheck = _nextRecheck,
                        setId = _setId,
                        checkCount = _checkCount,
                        positiveCheckCount = _positiveCheckCount
                    )

                }

            }
        } catch (e: Exception) {
            println(e)
        }

        return Pair(sets, cards)
    }

    fun exportToCsv(outputStream: FileOutputStream) {
        val text = (
                mutableListOf("setId;setName;cardId;frontText;backText;currentInterval;nextRecheck;checkCount;positiveCheckCount") +
                        Dependencies.database.getCardsWithSetNames().map { card -> card.getCsv() }
                ).joinToString(separator = "\r\n")
        BufferedWriter(OutputStreamWriter(outputStream, Charsets.UTF_8)).use { writer ->
            writer.write(text)
        }
    }
}