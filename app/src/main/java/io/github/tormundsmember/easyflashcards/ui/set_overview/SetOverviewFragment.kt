package io.github.tormundsmember.easyflashcards.ui.set_overview

import android.animation.Animator
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.AnimationListener
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseAdapter
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_set.DialogAddEditSet
import io.github.tormundsmember.easyflashcards.ui.more.MoreKey
import io.github.tormundsmember.easyflashcards.ui.play.PlayKey
import io.github.tormundsmember.easyflashcards.ui.set.SetKey
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.util.gone
import io.github.tormundsmember.easyflashcards.ui.util.visible
import kotlin.properties.Delegates

class SetOverviewFragment : BaseFragment() {

    override val layoutId: Int = R.layout.screen_all_sets
    private val adapter: Adapter =
        Adapter(onSomethingSelected = { isSomethingSelected ->
            shouldShowButtons = isSomethingSelected
        }, onClick = {
            goToSet(it.id)
        })


    private var shouldShowButtons: Boolean by Delegates.observable(false) { _, old, new ->
        if (old != new) {
            animateButtons(new)
        }
    }

    private val viewModel: SetOverviewViewModel by lazy { getViewModel<SetOverviewViewModel>() }

    private var tutorialStep: TutorialStep = TutorialStep.STEP1

    private lateinit var txtNoItems: TextView
    private lateinit var vTutorialBack: View
    private lateinit var txtTutorialPlay: TextView
    private lateinit var txtTutorialPlayInverse: TextView
    private lateinit var txtTutorialOk: TextView
    private lateinit var btnPlay: AppCompatImageButton
    private lateinit var btnPlayInverse: AppCompatImageButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.list_sets).let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(view.context)
        }



        txtNoItems = view.findViewById(R.id.txtNoItems)
        vTutorialBack = view.findViewById(R.id.vTutorialBack)
        txtTutorialPlay = view.findViewById(R.id.txtTutorialPlay)
        txtTutorialPlayInverse = view.findViewById(R.id.txtTutorialPlayInverse)
        txtTutorialOk = view.findViewById(R.id.txtTutorialOk)
        btnPlay = view.findViewById(R.id.btnPlay)
        btnPlayInverse = view.findViewById(R.id.btnPlayInverse)

        viewModel.sets.observe {
            adapter.items = it ?: emptyList()
            if (it?.isNotEmpty() == true) {
                txtNoItems.gone()
            } else {
                txtNoItems.visible()
            }
        }

        btnPlay.setOnClickListener {
            playSets(false)
        }
        btnPlayInverse.setOnClickListener {
            playSets(true)
        }

//        if (!Dependencies.userData.hasSeenSetsTutorial) {
//            Dependencies.userData.hasSeenSetsTutorial = true
//            showTutorial()
//        }
//        vTutorialBack.setOnClickListener {
//            nextTutorialStep()
//        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_set_overview, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val ctx = context
        if (ctx != null) {
            when (item.itemId) {
                R.id.action_add -> DialogAddEditSet.show(
                    context = ctx,
                    onSetAdded = {
                        goTo(SetKey(it))
                    },
                    onDeleted = {}
                )
                R.id.action_select_all -> {
                    adapter.activateAllItems()
                }
                R.id.action_more -> {
                    adapter.deactiveAllItems()
                    goTo(MoreKey())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun canGoBack(): Boolean {
        return if (adapter.getSelectedItems().isNotEmpty()) {
            adapter.deactiveAllItems()
            false
        } else {
            super.canGoBack()
        }
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


    private fun goToSet(setId: Int) {
        adapter.deactiveAllItems()
        goTo(SetKey(setId))
    }

    private fun animateButtons(isSomethingSelected: Boolean) {
        if (isSomethingSelected) {
            listOf(btnPlay, btnPlayInverse).forEach {
                it.elevation = 0F
                it.alpha = 0F
                it.visible()
                it.animate()
                    .alpha(1F)
                    .setDuration(300)
                    .setListener(null)
                    .start()
                it.animate()
                    .z(8F)
                    .setDuration(300)
                    .setStartDelay(300)
                    .setListener(null)
                    .start()
            }
        } else {
            listOf(btnPlay, btnPlayInverse).forEach {
                it.animate()
                    .alpha(0F)
                    .setDuration(300)
                    .z(0F)
                    .setListener(object : AnimationListener() {
                        override fun onAnimationEnd(animation: Animator?) {
                            it.gone()
                        }
                    })
                    .start()
            }
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

    private fun playSets(flipCards: Boolean) {
        goTo(PlayKey(adapter.getSelectedItems().map { it.id }, flipCards))
        adapter.deactiveAllItems()
    }

    private class Adapter(onSomethingSelected: (Boolean) -> Unit, onClick: (Set) -> Unit) :
        BaseAdapter<Set>(onSomethingSelected = onSomethingSelected, onClick = onClick) {
        override fun getItemLayoutId(viewType: Int): Int = R.layout.listitem_set

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            with(holder.itemView) {
                val txtSetName: TextView = findViewById(R.id.txtSetName)
                val viewSelected: View = findViewById(R.id.viewSelected)
                isLongClickable = true
                txtSetName.text = items[holder.adapterPosition].name
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