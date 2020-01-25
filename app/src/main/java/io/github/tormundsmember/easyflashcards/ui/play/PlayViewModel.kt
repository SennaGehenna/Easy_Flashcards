package io.github.tormundsmember.easyflashcards.ui.play

import androidx.lifecycle.LiveData
import io.github.tormundsmember.easyflashcards.ui.play.model.Game
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseViewModel

class PlayViewModel : BaseViewModel() {

    private lateinit var game: Game


    val isFinished: LiveData<Boolean>
        get() = game.isFinished
    val currentCard: LiveData<Game.FlippableCard>
        get() = game.currentCard

    val guesses: Int
        get() = game.guesses
    val correctGuesses: Int
        get() = game.correctGuesses

    fun initialize(setIds: List<Int>, isInverse: Boolean) {

        val setsById = Dependencies.database.getCardsByMultipleSetIds(setIds)
        val cards = setsById.map { Game.FlippableCard(it, isInverse, false) }.shuffled().toMutableList()
        game = Game(cards, Dependencies.database)
    }


    fun flipCard() {
        game.flipCard()
    }

    fun nextCard(correctGuess: Boolean) {
        game.nextCard(correctGuess)
    }

}