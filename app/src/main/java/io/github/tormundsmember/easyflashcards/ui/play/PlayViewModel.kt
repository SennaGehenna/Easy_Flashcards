package io.github.tormundsmember.easyflashcards.ui.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.util.plusAssign

class PlayViewModel : BaseViewModel() {

    private lateinit var cards: List<FlippableCard>


    val guesses: Int
        get() = cards.count { it.isCorrectGuess != null }
    val correctGuesses: Int
        get() = cards.count { it.isCorrectGuess == true }
    val cardCount: Int
        get() = cards.size

    private var wasLastGuessCorrect: Boolean? = null

    internal var currentCardIndex: Int = 0
        set(value) {
            field = value
            if (cards.isNotEmpty()) {
                _currentCard += cards.getOrNull(value)
            }
            _canUndoCard += value > 0
        }

    private val _canUndoCard = MutableLiveData(false)
    val canUndoCard: LiveData<Boolean>
        get() = _canUndoCard

    private val _isFinished: MutableLiveData<GameState> = MutableLiveData()
    val isFinished: LiveData<GameState>
        get() = _isFinished

    private val _currentCard: MutableLiveData<FlippableCard> = MutableLiveData<FlippableCard>()

    val currentCard: LiveData<FlippableCard>
        get() = _currentCard

    fun flipCard() {
        _currentCard += _currentCard.value?.let {
            it.copy(isFlipped = !it.isFlipped)
        }
    }

    fun nextCard(correctGuess: Boolean) {
        wasLastGuessCorrect = correctGuess

        with(cards[currentCardIndex]) {
            isCorrectGuess = correctGuess
            val nextInterval: RehearsalInterval
            val positiveCheckCount: Int
            val nextRecheck: Long
            if (Dependencies.userData.useSpacedRepetition) {
                if (correctGuess) {
                    nextInterval = card.currentInterval.getNext(Dependencies.userData.doNotShowLearnedCards)
                    positiveCheckCount = card.positiveCheckCount + 1
                } else {
                    nextInterval = RehearsalInterval.STAGE_1
                    positiveCheckCount = card.positiveCheckCount
                }
                nextRecheck =
                    io.github.tormundsmember.easyflashcards.ui.util.getStartOfDay() + java.util.concurrent.TimeUnit.DAYS.toMillis(
                        nextInterval.getInterval().toLong()
                    )
            } else {
                nextInterval = card.currentInterval
                positiveCheckCount = card.positiveCheckCount + if (correctGuess) 1 else 0
                nextRecheck = card.nextRecheck
            }


            Dependencies.database.addOrUpdateCard(
                card.copy(
                    currentInterval = nextInterval,
                    checkCount = card.checkCount + 1,
                    nextRecheck = nextRecheck,
                    positiveCheckCount = positiveCheckCount
                )
            )
        }



        if ((currentCardIndex + 1) < cards.size) {
            currentCardIndex++
        } else {
            _isFinished += GameState.Finished
        }
    }

    fun undo() {
        currentCardIndex--
    }

    data class FlippableCard(
        val card: Card,
        val isReverse: Boolean,
        val isFlipped: Boolean
    ) {
        var isCorrectGuess: Boolean? = null
    }

    fun initialize(setIds: List<Int>, isInverse: Boolean) {

        with(Dependencies) {
            val setsById = if (userData.useSpacedRepetition) {
                database.getCardsByMultipleSetIdsWithSpacedRepetion(setIds)
            } else {
                database.getCardsByMultipleSetIds(setIds)
            }
            val cardsTmp = setsById.map { FlippableCard(it, isInverse, false) }.shuffled()

            cards = if (userData.limitCards && userData.limitCardsAmount > 0) {
                cardsTmp.take(userData.limitCardsAmount)
            } else {
                cardsTmp
            }.toMutableList()

            if (cards.isEmpty()) {
                _isFinished += GameState.NoCardsToRehearse
            } else {
                _currentCard += cards.getOrNull(0)
                _isFinished += GameState.Running
            }
        }
    }


    sealed class GameState {

        object Running : GameState()
        object Finished : GameState()
        object NoCardsToRehearse : GameState()

    }
}