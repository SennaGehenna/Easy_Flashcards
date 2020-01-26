package io.github.tormundsmember.easyflashcards.ui.set

import android.animation.Animator
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
import io.github.tormundsmember.easyflashcards.ui.base_ui.AnimationListener
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseAdapter
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_card.DialogAddEditCard
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_set.DialogAddEditSet
import io.github.tormundsmember.easyflashcards.ui.more.MoreKey
import io.github.tormundsmember.easyflashcards.ui.play.PlayKey
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.util.gone
import io.github.tormundsmember.easyflashcards.ui.util.visible

open class SetFragment : BaseFragment() {

    override val layoutId: Int = R.layout.screen_set_overview

    private lateinit var vTutorialBack: View
    private lateinit var txtTutorialPlay: AppCompatTextView
    private lateinit var txtTutorialPlayInverse: AppCompatTextView
    private lateinit var txtTutorialOk: View
    private lateinit var txtNoItems: View
    private lateinit var btnPlay: View
    private lateinit var btnPlayInverse: View


    private var tutorialStep: TutorialStep = TutorialStep.STEP1

    private val adapter: MyAdapter = MyAdapter {
        context?.let { ctx ->
            DialogAddEditCard.show(ctx, it.setId, it)
        }
    }
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

        if (!Dependencies.userData.hasSeenSetOverviewTutorial) {
            Dependencies.userData.hasSeenSetOverviewTutorial = true
            showTutorial()
        }
        vTutorialBack.setOnClickListener {
            nextTutorialStep()
        }

        view.findViewById<RecyclerView>(R.id.list_sets).let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(view.context)
        }

        viewModel.cards.observe {
            adapter.items = it ?: emptyList()
            txtNoItems.visibility = when {
                it == null || it.isNotEmpty() -> View.GONE
                else -> View.VISIBLE
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

        activity?.actionBar?.title = Dependencies.database.getSetById(getKey<SetKey>().setId).name
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_set, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val ctx = context
        if (ctx != null) {
            when (item.itemId) {
                R.id.action_add -> DialogAddEditCard.show(ctx, getKey<SetKey>().setId)
                R.id.action_edit -> DialogAddEditSet.show(
                    context = ctx,
                    setId = getKey<SetKey>().setId,
                    onSetAdded = { },
                    onDeleted = {
                        goBack()
                    })
                R.id.action_more -> {
                    goTo(MoreKey())
                    adapter.deactiveAllItems()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTutorial() {
        vTutorialBack.alpha = 0F
        vTutorialBack.visible()
        vTutorialBack.animate()
            .alpha(0.8F)
            .setDuration(300)
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
                .start()
        }
    }

    private fun nextTutorialStep() {
        tutorialStep = when (tutorialStep) {
            TutorialStep.STEP1 -> {
                txtTutorialPlay.animate()
                    .alpha(0F)
                    .setDuration(300)
                    .setListener(object : AnimationListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            txtTutorialPlay.gone()
                        }
                    })
                    .start()
                txtTutorialPlayInverse.alpha = 0F
                txtTutorialPlayInverse.visible()
                txtTutorialPlayInverse.animate()
                    .alpha(1F)
                    .setDuration(300)
                    .setStartDelay(400)
                    .start()
                TutorialStep.STEP2
            }
            TutorialStep.STEP2 -> {
                listOf(
                    vTutorialBack,
                    txtTutorialOk,
                    txtTutorialPlayInverse
                ).forEach {
                    it.animate()
                        .alpha(0F)
                        .setDuration(300)
                        .setListener(object : AnimationListener() {
                            override fun onAnimationEnd(animation: Animator?) {
                                it.gone()
                            }
                        })
                        .start()
                }
                TutorialStep.DONE
            }
            TutorialStep.DONE -> TutorialStep.DONE
        }
    }

    protected class MyAdapter(onClick: (Card) -> Unit) : BaseAdapter<Card>(onClick = onClick) {
        override fun getItemLayoutId(viewType: Int): Int = R.layout.listitem_card

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            with(holder.itemView) {

                val txtOriginalTerm: TextView = findViewById(R.id.txtOriginalTerm)
                val txtTranslatedTerm: TextView = findViewById(R.id.txtTranslatedTerm)
                val viewSelected: View = findViewById(R.id.viewSelected)

                isLongClickable = true
                txtOriginalTerm.text = items[holder.adapterPosition].frontText
                txtTranslatedTerm.text = items[holder.adapterPosition].backText
                viewSelected.setBackgroundResource((activeItems.contains(position)).mapActiveColor())
                setOnClickListener {
                    onClick(items[holder.adapterPosition])
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