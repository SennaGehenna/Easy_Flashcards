package io.github.tormundsmember.easyflashcards.ui.set.model

enum class RehearsalInterval {

    STAGE_1,
    STAGE_2,
    STAGE_3,
    STAGE_4,
    STAGE_5,
    DONE;

    fun getNext() = when (this) {
        STAGE_1 -> STAGE_2
        STAGE_2 -> STAGE_3
        STAGE_3 -> STAGE_4
        STAGE_4 -> STAGE_5
        STAGE_5 -> DONE
        DONE -> DONE
    }

    fun getInterval() = when (this) {
        STAGE_1 -> 1
        STAGE_2 -> 3
        STAGE_3 -> 7
        STAGE_4 -> 14
        STAGE_5 -> 20
        DONE -> Int.MAX_VALUE
    }


    companion object{
        fun getFromInterval(interval: Int): RehearsalInterval {
            return when (interval) {
                1 -> STAGE_1
                3 -> STAGE_2
                7 -> STAGE_3
                14 -> STAGE_4
                20 -> STAGE_5
                else -> DONE
            }
        }
    }
}