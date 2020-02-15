package io.github.tormundsmember.easyflashcards.ui.set

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseAdapter
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.MainScreen
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_card.DialogAddEditCard
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_set.DialogAddEditSet
import io.github.tormundsmember.easyflashcards.ui.more.MoreKey
import io.github.tormundsmember.easyflashcards.ui.play.PlayKey
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.settings.SettingsKey
import io.github.tormundsmember.easyflashcards.ui.util.*

open class SetFragment : BaseFragment() {

    override val layoutId: Int = R.layout.screen_set
    override val titleText: String
        get() = Dependencies.database.getSetById(getKey<SetKey>().setId).name

    private lateinit var vTutorialBack: View
    private lateinit var txtTutorialPlay: AppCompatTextView
    private lateinit var txtTutorialPlayInverse: AppCompatTextView
    private lateinit var txtTutorialOk: View
    private lateinit var txtNoItems: View
    private lateinit var btnPlay: View
    private lateinit var btnPlayInverse: View

    private var somethingSelected: Boolean = false
    private var tutorialStep: TutorialStep = TutorialStep.STEP1

    private val adapter: MyAdapter = MyAdapter(onClick = {
        context?.let { ctx ->
            showCardAddEditDialog(ctx, it)
        }
    }, onSomethingSelected = {
        somethingSelected = it
        activity?.invalidateOptionsMenu()
    })
    private val viewModel: SetViewModel
        get() = getViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.init(getKey<SetKey>().setId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vTutorialBack = view.findViewById(R.id.vTutorialBack)
        txtTutorialPlay = view.findViewById(R.id.txtTutorialPlay)
        txtTutorialPlayInverse = view.findViewById(R.id.txtTutorialPlayInverse)
        txtTutorialOk = view.findViewById(R.id.txtTutorialOk)
        txtNoItems = view.findViewById(R.id.txtNoItems)
        btnPlay = view.findViewById(R.id.btnPlay)
        btnPlayInverse = view.findViewById(R.id.btnPlayInverse)


        vTutorialBack.setOnClickListener {
            nextTutorialStep()
        }

        with(Dependencies) {
            if (!userData.hasSeenSetOverviewTutorial) {
                txtNoItems.gone()
                val ctx = activity
                (ctx as? MainScreen)?.showCardsTutorial(
                    database.getSetById(getKey<SetKey>().setId).name,
                    onAddButtonClick = {
                        txtNoItems.animateVisible()
                        userData.hasSeenSetOverviewTutorial = true
                        showCardAddEditDialog(ctx,null)
                    },
                    onCancel = {
                        userData.hasSeenSetOverviewTutorial = true
                        txtNoItems.animateVisible()
                    }
                )
            }
        }

        view.findViewById<RecyclerView>(R.id.list_sets).let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(view.context)
        }

        viewModel.cards.observe {
            if (it != null && it.isNotEmpty()) {
                btnPlay.visible()
                btnPlayInverse.visible()
            } else {
                btnPlay.gone()
                btnPlayInverse.gone()
            }
            adapter.items = it ?: emptyList()

            txtNoItems.visibility = when {
                !Dependencies.userData.hasSeenSetOverviewTutorial || it == null || it.isNotEmpty() -> View.GONE
                else -> View.VISIBLE
            }
            if (adapter.items.isNotEmpty()) {
                with(Dependencies.userData) {
                    if (hasSeenSetOverviewTutorial && !hasSeenSetOverviewTutorialWithExistingItems) {
                        hasSeenSetOverviewTutorialWithExistingItems = true
                        showTutorial()
                    }
                }
            }
        }

        btnPlay.setOnClickListener {
            adapter.deactiveAllItems()
            goTo(PlayKey(setId = getKey<SetKey>().setId, reverseCards = false))
        }
        btnPlayInverse.setOnClickListener {
            adapter.deactiveAllItems()
            goTo(PlayKey(setId = getKey<SetKey>().setId, reverseCards = true))
        }

        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (somethingSelected)
            inflater.inflate(R.menu.menu_set_cards_selected, menu)
        else
            inflater.inflate(R.menu.menu_set, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun showCardAddEditDialog(context: Context, card: Card?) {
        with(Dependencies.userData) {
            hasSeenSetOverviewTutorial = true
            DialogAddEditCard.show(context, getKey<SetKey>().setId, card) {
                if (!hasSeenSetOverviewTutorialWithExistingItems) {
                    hasSeenSetOverviewTutorialWithExistingItems = true
                    showTutorial()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val ctx = context
        if (ctx != null) {
            when (item.itemId) {
                R.id.action_add -> showCardAddEditDialog(ctx, null)
                R.id.action_edit -> DialogAddEditSet.show(
                    context = ctx,
                    setId = getKey<SetKey>().setId,
                    onSetAdded = {
                        activity?.title = Dependencies.database.getSetById(getKey<SetKey>().setId).name
                    },
                    onDeleted = {
                        goBack()
                    })
                R.id.action_settings -> {
                    goTo(SettingsKey())
                    adapter.deactiveAllItems()
                }
                R.id.action_more -> {
                    goTo(MoreKey())
                    adapter.deactiveAllItems()
                }
                R.id.action_flip_selected -> {
                    viewModel.flipCards(adapter.getSelectedItems())
                    adapter.deactiveAllItems()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTutorial() {
        btnPlayInverse.elevation = 0F
        vTutorialBack.alpha = 0F
        vTutorialBack.visible()
        vTutorialBack.animate()
            .alpha(0.8F)
            .setDuration(300)
            .resetListener()
            .start()
        listOf(
            txtTutorialPlay,
            txtTutorialOk
        ).forEach {
            it.alpha = 0F
            it.visible()
            it.animate()
                .alpha(1F)
                .setStartDelay(400)
                .setDuration(300)
                .resetListener()
                .start()
        }
    }

    private fun nextTutorialStep() {
        tutorialStep = when (tutorialStep) {
            TutorialStep.STEP1 -> {
                btnPlay.animate().z(0F).setDuration(300).resetListener().start()
                btnPlayInverse.animate().z(8F).setDuration(300).resetListener().start()
                txtTutorialPlay.animate()
                    .alpha(0F)
                    .setDuration(300)
                    .setListener(
                        onAnimationEnd = {
                            txtTutorialPlay.invisible()
                        }
                    )
                    .start()
                txtTutorialPlayInverse.alpha = 0F
                txtTutorialPlayInverse.visible()
                txtTutorialPlayInverse.animate()
                    .alpha(1F)
                    .setDuration(300)
                    .setStartDelay(400)
                    .resetListener()
                    .start()
                TutorialStep.STEP2
            }
            TutorialStep.STEP2 -> {
                btnPlay.animate().z(0F).setDuration(300).resetListener().start()
                listOf(
                    vTutorialBack,
                    txtTutorialOk,
                    txtTutorialPlayInverse
                ).forEach {
                    it.animate()
                        .alpha(0F)
                        .setDuration(300)
                        .setListener(
                            onAnimationEnd = {
                                it.gone()
                            }
                        )
                        .start()
                }
                TutorialStep.DONE
            }
            TutorialStep.DONE -> {
                TutorialStep.DONE
            }
        }
    }

    protected class MyAdapter(onClick: (Card) -> Unit, onSomethingSelected: ((Boolean) -> Unit)) :
        BaseAdapter<Card>(onClick = onClick, onSomethingSelected = onSomethingSelected) {
        override fun getItemLayoutId(viewType: Int): Int = R.layout.listitem_card

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            with(holder.itemView) {

                val txtOriginalTerm: TextView = findViewById(R.id.txtOriginalTerm)
                val txtTranslatedTerm: TextView = findViewById(R.id.txtTranslatedTerm)
                val viewSelected: View = findViewById(R.id.viewSelected)
                txtTranslatedTerm.text = items[holder.adapterPosition].backText
                txtOriginalTerm.text = items[holder.adapterPosition].frontText

                isLongClickable = true
                viewSelected.setBackgroundResource((activeItems.contains(position)).mapActiveColor())
                viewSelected.visibility = activeItems.contains(position).mapToVisibility()
                setOnClickListener {
                    if (activeItems.isNotEmpty()) {
                        holder.longClick(this)
                    } else {
                        onClick(items[holder.adapterPosition])
                    }
                }
                setOnLongClickListener(holder.longClick)
            }
        }
    }


    private sealed class TutorialStep {
        object STEP1 : TutorialStep()
        object STEP2 : TutorialStep()
        object DONE : TutorialStep()
    }
}