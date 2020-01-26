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

    val guesses: Int
        get() = game.guesses
    val correctGuesses: Int
        get() = game.correctGuesses

    fun initialize(setIds: List<Int>, isInverse: Boolean) {

        val setsById = if (Dependencies.userData.useSpacedRepetition) {
            Dependencies.database.getCardsByMultipleSetIdsWithSpacedRepetion(setIds)
        } else {
            Dependencies.database.getCardsByMultipleSetIds(setIds)
        }
        val cards = setsById.map { Game.FlippableCard(it, isInverse, false) }.shuffled().toMutableList()
        game = Game(cards, Dependencies.database)
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