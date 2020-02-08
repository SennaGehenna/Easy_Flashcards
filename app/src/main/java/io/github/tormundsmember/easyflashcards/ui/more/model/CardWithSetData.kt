package io.github.tormundsmember.easyflashcards.ui.more.model

import io.github.tormundsmember.easyflashcards.ui.set.model.Date
import io.github.tormundsmember.easyflashcards.ui.set.model.Day
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval

class CardWithSetData(
    val setId: Int,
    val name: String,
    val cardId: Int,
    val frontText: String,
    val backText: String,
    val currentInterval: RehearsalInterval,
    val nextRecheck: Date,
    val checkCount: Int,
    val positiveCheckCount: Int
) {

    fun getCsv(): String {
        return listOf(
            setId.toString(),
            name,
            cardId.toString(),
            frontText,
            backText,
            currentInterval.toString(),
            nextRecheck.toString(),
            checkCount.toString(),
            positiveCheckCount.toString()
        ).joinToString(
            separator = ";"
        )
    }


}