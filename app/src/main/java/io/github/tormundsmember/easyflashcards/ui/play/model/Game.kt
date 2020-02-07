package io.github.tormundsmember.easyflashcards.ui.play.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.tormundsmember.easyflashcards.data.Database
import io.github.tormundsmember.easyflashcards.ui.play.PlayViewModel
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.util.plusAssign
import java.util.concurrent.TimeUnit

class Game(
    private val cards: MutableList<FlippableCard>,
    private val database: Database
) {

    var guesses: Int = 0
        private set
    var correctGuesses: Int = 0
        private set

    private var wasLastGuessCorrect: Boolean? = null

    private var currentCardIndex: Int = 0
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
        guesses++
        if (correctGuess) correctGuesses++


        val card = cards[currentCardIndex].card

        val nextInterval: RehearsalInterval
        val positiveCheckCount: Int
        if (correctGuess) {
            nextInterval = card.currentInterval.getNext()
            positiveCheckCount = card.positiveCheckCount + 1
        } else {
            nextInterval = RehearsalInterval.STAGE_1
            positiveCheckCount = card.positiveCheckCount
        }

        val nextRecheck = System.currentTimeMillis().let { currentTime ->
            TimeUnit.MILLISECONDS.toDays(currentTime).let { asDay ->
                TimeUnit.DAYS.toMillis(asDay).let {
                    it + TimeUnit.DAYS.toMillis(nextInterval.getInterval().toLong())
                }
            }
        }

        database.addOrUpdateCard(
            card.copy(
                currentInterval = nextInterval,
                checkCount = card.checkCount + 1,
                nextRecheck = nextRecheck,
                positiveCheckCount = positiveCheckCount
            )
        )



        if ((currentCardIndex + 1) < cards.size) {
            currentCardIndex++
        } else {
            _isFinished += PlayViewModel.GameState.Finished
        }
    }

    fun undo() {
        currentCardIndex--
        wasLastGuessCorrect?.let {
            if (it) {
                correctGuesses--
            }
        }
        guesses--
    }

    data class FlippableCard(
        val card: Card,
        val isReverse: Boolean,
        val isFlipped: Boolean
    )
}