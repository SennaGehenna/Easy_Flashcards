package io.github.tormundsmember.easyflashcards.ui.play.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.tormundsmember.easyflashcards.data.Database
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.play.PlayViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.util.getStartOfDay
import io.github.tormundsmember.easyflashcards.ui.util.plusAssign
import java.util.concurrent.TimeUnit

class Game(
    private val cards: MutableList<FlippableCard>,
    private val database: Database
) {

    val guesses: Int
        get() = cards.count { it.isCorrectGuess != null }
    val correctGuesses: Int
        get() = cards.count { it.isCorrectGuess == true }
    val cardCount = cards.size

    private var wasLastGuessCorrect: Boolean? = null

    internal var currentCardIndex: Int = 0
        set(value) {
            field = value
            if (cards.isNotEmpty()) {
                _currentCard += cards.getOrNull(value)
            }
            _canUndoCard += value > 0
        }

    private val _canUndoCard = MutableLiveData<Boolean>().apply { postValue(false) }
    val canUndoCard: LiveData<Boolean>
        get() = _canUndoCard

    private val _isFinished: MutableLiveData<PlayViewModel.GameState> = MutableLiveData()
    val isFinished: LiveData<PlayViewModel.GameState>
        get() = _isFinished

    private val _currentCard: MutableLiveData<FlippableCard> = MutableLiveData<FlippableCard>().apply {
        this += cards.getOrNull(0)
    }
    val currentCard: LiveData<FlippableCard>
        get() = _currentCard

    init {
        if (cards.isEmpty()) {
            _isFinished += PlayViewModel.GameState.NoCardsToRehearse
        } else {
            _isFinished += PlayViewModel.GameState.Running
        }
    }

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
                nextRecheck = getStartOfDay() + TimeUnit.DAYS.toMillis(nextInterval.getInterval().toLong())
            } else {
                nextInterval = card.currentInterval
                positiveCheckCount = card.positiveCheckCount + if (correctGuess) 1 else 0
                nextRecheck = card.nextRecheck
            }


            database.addOrUpdateCard(
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
            _isFinished += PlayViewModel.GameState.Finished
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
}