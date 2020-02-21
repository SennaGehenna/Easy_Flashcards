package io.github.tormundsmember.easyflashcards.ui.more

import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MoreViewModel : BaseViewModel() {


    suspend fun importFromCsv(inputStream: InputStream): Int {
        val (sets, cards) = obtainImportedData(inputStream)
        with(Dependencies.database) {
            addSets(sets)
            addOrUpdateCards(cards)
        }
        return cards.size
    }

    fun obtainImportedData(inputStream: InputStream): Pair<List<Set>, List<Card>> {
        fun List<String>.getOrNullWithNullableKey(key: Int?) = if (key != null) this.getOrNull(key) else null

        val csv: List<String> = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use {
            it.readLines()
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
            frontTextKey,
            backTextKey
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

        var lastSetId = 0

        try {
            if (columnNames.keys.containsAll(requiredColumns)) {
                csv.drop(1).map { it.split(";") }.forEachIndexed { index, row: List<String> ->
                    if (columnNames.keys.contains(setIdKey)) {
                        if (sets.none { it.id == row[setId!!].toInt() }) {
                            val set = Set(row[setId!!].toInt(), row[setName!!])
                            sets += set
                        }
                    } else {
                        if (columnNames.keys.contains(setNameKey)) {
                            val setNameExtracted = row[setName!!]
                            if (sets.none { it.name == setNameExtracted })
                                sets += Set(++lastSetId, setNameExtracted)
                        } else {
                            if (sets.isEmpty()) {
                                sets += Set(
                                    ++lastSetId,
                                    "import_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}"
                                )
                            }
                        }
                    }
                    val _id = row.getOrNullWithNullableKey(cardId)?.toInt() ?: index
                    val _frontText = row.getOrNullWithNullableKey(frontText)!!
                    val _backText = row.getOrNullWithNullableKey(backText)!!
                    val _currentInterval =
                        RehearsalInterval.TypeConverter.fromString(
                            row.getOrNullWithNullableKey(currentInterval) ?: RehearsalInterval.STAGE_1.name
                        )
                    val _nextRecheck =
                        row.getOrNullWithNullableKey(nextRecheck)?.toLong() ?: RehearsalInterval.getNextRehearsalDate(0)
                    val _setId = row.getOrNullWithNullableKey(setId)?.toInt() ?: 0
                    val _checkCount = row.getOrNullWithNullableKey(checkCount)?.toInt() ?: 0
                    val _positiveCheckCount = row.getOrNullWithNullableKey(positiveCheckCount)?.toInt() ?: 0
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
