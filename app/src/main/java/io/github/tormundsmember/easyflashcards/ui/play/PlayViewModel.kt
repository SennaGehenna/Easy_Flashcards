package io.github.tormundsmember.easyflashcards.ui.play

import androidx.lifecycle.LiveData
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel
import io.github.tormundsmember.easyflashcards.ui.play.model.Game

class PlayViewModel : BaseViewModel() {

    private lateinit var game: Game


    val isFinished: LiveData<GameState>
        get() = game.isFinished
    val currentCard: LiveData<Game.FlippableCard>
        get() = game.currentCard
    val canUndoCard: LiveData<Boolean>
        get() = game.canUndoCard

    val guesses: Int
        get() = game.guesses
    val correctGuesses: Int
        get() = game.correctGuesses
    val cardCount: Int
        get() = game.cardCount
    val currentCardIndex: Int
        get() = game.currentCardIndex

    fun initialize(setIds: List<Int>, isInverse: Boolean) {

        with(Dependencies) {
            val setsById = if (userData.useSpacedRepetition) {
                database.getCardsByMultipleSetIdsWithSpacedRepetion(setIds)
            } else {
                database.getCardsByMultipleSetIds(setIds)
            }
            val cardsTmp = setsById.map { Game.FlippableCard(it, isInverse, false) }.shuffled()

            val cards = if (userData.limitCards && userData.limitCardsAmount > 0) {
                cardsTmp.take(userData.limitCardsAmount)
            } else {
                cardsTmp
            }.toMutableList()

            game = Game(cards, database)
        }
    }

    fun undo() {
        game.undo()
    }

    fun flipCard() {
        game.flipCard()
    }

    fun nextCard(correctGuess: Boolean) {
        game.nextCard(correctGuess)
    }


    sealed class GameState {

        object Running : GameState()
        object Finished : GameState()
        object NoCardsToRehearse : GameState()

    }
}