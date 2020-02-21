package io.github.tormundsmember.easyflashcards.ui.set.model

import java.util.concurrent.TimeUnit

enum class RehearsalInterval {

    STAGE_1,
    STAGE_2,
    STAGE_3,
    STAGE_4,
    STAGE_5,
    DONE;

    fun getNext(doNotShowLearnedCards: Boolean) = when (this) {
        STAGE_1 -> STAGE_2
        STAGE_2 -> STAGE_3
        STAGE_3 -> STAGE_4
        STAGE_4 -> STAGE_5
        STAGE_5, DONE -> if (doNotShowLearnedCards) DONE else STAGE_5
    }

    fun getInterval() = when (this) {
        STAGE_1 -> 1
        STAGE_2 -> 3
        STAGE_3 -> 7
        STAGE_4 -> 14
        STAGE_5 -> 20
        DONE -> Int.MAX_VALUE
    }


    object TypeConverter {

        @androidx.room.TypeConverter
        @JvmStatic
        fun toString(interval: RehearsalInterval) = interval.name

        @androidx.room.TypeConverter
        @JvmStatic
        fun fromString(string: String) = valueOf(string)
    }


    companion object {
        fun getNextRehearsalDate(daysToAdd: Long) =
            System.currentTimeMillis().let { currentTime ->
                TimeUnit.MILLISECONDS.toDays(currentTime).let { asDay ->
                    TimeUnit.DAYS.toMillis(asDay).let {
                        it + TimeUnit.DAYS.toMillis(daysToAdd)
                    }
                }
            }
    }
}

