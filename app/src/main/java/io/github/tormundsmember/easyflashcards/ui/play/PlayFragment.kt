package io.github.tormundsmember.easyflashcards.ui.play

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.PlayButtonsLayout
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.more.MoreKey
import io.github.tormundsmember.easyflashcards.ui.settings.SettingsKey
import io.github.tormundsmember.easyflashcards.ui.util.*
import java.util.concurrent.atomic.AtomicBoolean

class PlayFragment : BaseFragment() {

    override val layoutId: Int = R.layout.screen_play

    private val viewModel: PlayViewModel by lazy {
        @Suppress("RemoveExplicitTypeArguments") //doesn't compile otherwise
        getViewModel<PlayViewModel>().apply {
            val key = getKey<PlayKey>()
            initialize(key.setIds, key.reverseCards)
        }
    }

    private lateinit var vCard: CardView
    private lateinit var txtCardText: AppCompatTextView
    private lateinit var vPlayButtons: PlayButtonsLayout
    private lateinit var txtCardsRehearsed: AppCompatTextView
    private lateinit var txtCardCorrectlyGuessed: AppCompatTextView
    private lateinit var btnOkay: AppCompatButton
    private lateinit var lblCards: AppCompatTextView
    private lateinit var lblCardsGuessed: AppCompatTextView
    private lateinit var txtNoCardsToRehease: AppCompatTextView
    private lateinit var lblCardCount: AppCompatTextView
    private lateinit var lblCurrentCardIndex: AppCompatTextView
    private lateinit var lblSeparator: AppCompatTextView

    private val canclickButtons = AtomicBoolean(true)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vCard = view.findViewById(R.id.vCard)
        txtCardText = view.findViewById(R.id.txtCardText)
        vPlayButtons = view.findViewById(R.id.vPlayButtons)
        txtCardsRehearsed = view.findViewById(R.id.txtCardsRehearsed)
        txtCardCorrectlyGuessed = view.findViewById(R.id.txtCardCorrectlyGuessed)
        btnOkay = view.findViewById(R.id.btnOkay)
        lblCards = view.findViewById(R.id.lblCards)
        lblCardsGuessed = view.findViewById(R.id.lblCardsGuessed)
        txtNoCardsToRehease = view.findViewById(R.id.txtNoCardsToRehease)
        lblCardCount = view.findViewById(R.id.lblCardCount)
        lblCurrentCardIndex = view.findViewById(R.id.lblCurrentCardIndex)
        lblSeparator = view.findViewById(R.id.lblSeparator)

        btnOkay.setOnClickListener {
            goBack()
        }

        viewModel.isFinished.observe {
            when (it) {
                PlayViewModel.GameState.Finished -> showSetDone()
                PlayViewModel.GameState.NoCardsToRehearse -> showNoCardsToRehease()
            }

        }

        viewModel.canUndoCard.observe {
            when (it) {
                true -> vPlayButtons.showUndoButton()
                false -> vPlayButtons.hideUndoButton()
            }
        }


        lblCardCount.text = viewModel.cardCount.toString()
        viewModel.currentCard.observe { currentCard ->

            lblCurrentCardIndex.text = viewModel.currentCardIndex.toString()

            if (currentCard != null) {
                if (currentCard.isFlipped) {
                    vCard.animate()
                        .scaleX(0F)
                        .setDuration(150)
                        .setListener(
                            onAnimationEnd = {
                                if (currentCard.isReverse) {
                                    txtCardText.text = currentCard.card.frontText
                                } else {
                                    txtCardText.text = currentCard.card.backText
                                }
                                vCard.animate()
                                    .scaleX(1F)
                                    .setDuration(150)
                                    .setListener(
                                        onAnimationEnd = {
                                            canclickButtons.set(true)
                                        }
                                    )
                                    .start()
                            }
                        )
                        .start()
                    vPlayButtons.showFeedbackButtons()
                } else {
                    vCard.animate()
                        .scaleX(0F)
                        .setDuration(150)
                        .setListener(
                            onAnimationEnd = {
                                if (currentCard.isReverse) {
                                    txtCardText.text = currentCard.card.backText
                                } else {
                                    txtCardText.text = currentCard.card.frontText
                                }
                                vCard.animate()
                                    .scaleX(1F)
                                    .setDuration(150)
                                    .setListener(
                                        onAnimationEnd = {
                                            canclickButtons.set(true)
                                        }
                                    )
                                    .start()
                            }
                        )
                        .start()
                    vPlayButtons.showFlipButton()
                }
            }
        }



        vPlayButtons.btnFlip.setThrottledOnClickListener {
            viewModel.flipCard()
        }

        vPlayButtons.btnPositive.setThrottledOnClickListener {
            viewModel.nextCard(true)
        }
        vPlayButtons.btnNegative.setThrottledOnClickListener {
            viewModel.nextCard(false)
        }
        vPlayButtons.btnUndo.setThrottledOnClickListener {
            viewModel.undo()
        }
        setHasOptionsMenu(true)
    }

    private inline fun View.setThrottledOnClickListener(crossinline action: Action) {
        setOnClickListener {
            if (canclickButtons.getAndSet(false)) {
                action()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_play, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val ctx = context
        if (ctx != null) {
            when (item.itemId) {
                R.id.action_more -> goTo(MoreKey())
                R.id.action_settings -> {
                    goTo(SettingsKey())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showNoCardsToRehease() {

        listOf(vCard, vPlayButtons, lblSeparator, lblCardCount, lblCurrentCardIndex).forEach {
            it.gone()
        }
        listOf<View>(
            txtNoCardsToRehease,
            btnOkay
        ).forEach {
            it.alpha = 0F
            it.visible()
            it.animate().alpha(1F)
                .setStartDelay(600)
                .setDuration(300)
                .resetListener()
        }
    }

    private fun showSetDone() {
        txtCardsRehearsed.text = getString(R.string.results_cards_rehearsed)
        txtCardCorrectlyGuessed.text = getString(R.string.results_cards_correctly_guessed)
        lblCards.text = viewModel.guesses.toString()
        lblCardsGuessed.text = viewModel.correctGuesses.toString()

        listOf(vCard, vPlayButtons, lblSeparator, lblCardCount, lblCurrentCardIndex).forEach {
            it.animate().alpha(0F)
                .setDuration(300)
                .setListener(
                    onAnimationEnd = {
                        it.gone()
                    }
                )
        }
        listOf<View>(
            txtCardsRehearsed,
            txtCardCorrectlyGuessed,
            lblCards,
            lblCardsGuessed,
            btnOkay
        ).forEach {
            it.alpha = 0F
            it.visible()
            it.animate().alpha(1F)
                .setStartDelay(600)
                .setDuration(300)
                .resetListener()
        }
    }
}