package io.github.tormundsmember.easyflashcards.ui.more

import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.base_ui.exceptions.MissingRequiredKeysException
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.util.getStartOfDay
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MoreViewModel : BaseViewModel() {

    @Throws(MissingRequiredKeysException::class)
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

        fun isKeyInCsv(key: String) = columnNames.keys.contains(key)

        val sets: MutableList<Set> = mutableListOf()
        val cards: MutableList<Card> = mutableListOf()

        val colSetId = columnNames[setIdKey]
        val colSetName = columnNames[setNameKey]
        val colCardId = columnNames[cardIdKey]
        val colFrontText = columnNames[frontTextKey]!!
        val colBackText = columnNames[backTextKey]!!
        val colCurrentInterval = columnNames[currentIntervalKey]
        val colNextRecheck = columnNames[nextRecheckKey]
        val colCheckCount = columnNames[checkCountKey]
        val colPositiveCheckCount = columnNames[positiveCheckCountKey]

        var lastSetId = 0

        if (columnNames.keys.containsAll(requiredColumns)) {
            csv.drop(1).map { it.split(";") }.forEachIndexed { index, row: List<String> ->
                if (isKeyInCsv(setIdKey)) {
                    if (sets.none { it.id == row[colSetId!!].toInt() }) {
                        val set = Set(row[colSetId!!].toInt(), row[colSetName!!])
                        sets += set
                    }
                } else {
                    if (isKeyInCsv(setNameKey)) {
                        val setNameExtracted = row[colSetName!!]
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
                val id: Int = row.getOrNullWithNullableKey(colCardId)?.toInt() ?: index + 1
                val frontText: String = row[colFrontText]
                val backText: String = row[colBackText]
                val currentInterval = row.getOrNullWithNullableKey(colCurrentInterval)?.let {
                    RehearsalInterval.valueOf(it)
                } ?: RehearsalInterval.STAGE_1
                val nextRecheck = row.getOrNullWithNullableKey(colNextRecheck)?.toLong() ?: getStartOfDay()
                val setId = row.getOrNullWithNullableKey(colSetId)?.toInt() ?: lastSetId
                val checkCount = row.getOrNullWithNullableKey(colCheckCount)?.toInt() ?: 0
                val positiveCheckCount = row.getOrNullWithNullableKey(colPositiveCheckCount)?.toInt() ?: 0

                cards += Card(
                    id = id,
                    frontText = frontText,
                    backText = backText,
                    currentInterval = currentInterval,
                    nextRecheck = nextRecheck,
                    setId = setId,
                    checkCount = checkCount,
                    positiveCheckCount = positiveCheckCount
                )

            }

        } else {
            val missingKeys = requiredColumns.filterNot { isKeyInCsv(it) }
            throw MissingRequiredKeysException("Not all required keys supplied. Missing keys", missingKeys)
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
