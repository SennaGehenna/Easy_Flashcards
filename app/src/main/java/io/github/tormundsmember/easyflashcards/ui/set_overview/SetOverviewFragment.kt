package io.github.tormundsmember.easyflashcards.ui.set_overview

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseAdapter
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.base_ui.MainScreen
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_set.DialogAddEditSet
import io.github.tormundsmember.easyflashcards.ui.more.MoreKey
import io.github.tormundsmember.easyflashcards.ui.play.PlayKey
import io.github.tormundsmember.easyflashcards.ui.set.SetKey
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.settings.SettingsKey
import io.github.tormundsmember.easyflashcards.ui.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private var tutorialStep: TutorialStep = TutorialStep.SHOW_MULTISELECT

    private lateinit var txtNoItems: TextView
    private lateinit var vTutorialBack: View
    private lateinit var txtTutorialPlay: TextView
    private lateinit var txtTutorialPlayInverse: TextView
    private lateinit var txtTutorialOk: TextView
    private lateinit var btnPlay: AppCompatImageButton
    private lateinit var btnPlayInverse: AppCompatImageButton
    private lateinit var txtTutorialSelect: TextView
    private lateinit var itemRoot: View
    private lateinit var card: CardView
    private lateinit var viewSelected: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.list_sets).let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(view.context)
        }

        with(view) {
            card = by(R.id.card)
            txtNoItems = by(R.id.txtNoItems)
            vTutorialBack = by(R.id.vTutorialBack)
            txtTutorialPlay = by(R.id.txtTutorialPlay)
            txtTutorialPlayInverse = by(R.id.txtTutorialPlayInverse)
            txtTutorialOk = by(R.id.txtTutorialOk)
            btnPlay = by(R.id.btnPlay)
            btnPlayInverse = by(R.id.btnPlayInverse)
            txtTutorialSelect = by(R.id.txtTutorialSelect)
            itemRoot = by(R.id.itemRoot)
            viewSelected = by(R.id.viewSelected)
            card = by(R.id.card)
        }

        viewModel.sets.observe {
            with(Dependencies.userData) {
                val items = it ?: emptyList()
                adapter.items = items
                if (items.isNotEmpty()) {
                    txtNoItems.gone()
                    if (items.size > 1) {
                        if (!hasSeenSetsTutorialWithExistingItems) {
                            hasSeenSetsTutorialWithExistingItems = true
                            showTutorial()
                        }
                    }
                } else {
                    if (hasSeenSetsTutorial) {
                        txtNoItems.visible()
                    }
                }
                activity?.invalidateOptionsMenu()
            }
        }

        btnPlay.setOnClickListener {
            playSets(false)
        }
        btnPlayInverse.setOnClickListener {
            playSets(true)
        }

        with(Dependencies.userData) {
            if (!hasSeenSetsTutorial) {
                val activity = activity
                (activity as? MainScreen)?.showSetsTutorial(onAddButtonClick = {
                    //region show NoItems
                    if (adapter.items.isEmpty()) {
                        txtNoItems.animateVisible()
                    }
                    //endregion
                    hasSeenSetsTutorial = true
                    showAddEditSetDialog(activity)
                }, onCancel = {
                    //region show NoItems
                    if (adapter.items.isEmpty()) {
                        txtNoItems.animateVisible()
                    }
                    //endregion
                    hasSeenSetsTutorial = true
                })
            }
        }

        vTutorialBack.setOnClickListener {
            nextTutorialStep()
        }

        setHasOptionsMenu(true)
    }

    private fun showAddEditSetDialog(context: Context) {
        DialogAddEditSet.show(
            context = context,
            onSetAdded = {
                goTo(SetKey(it))
            },
            onDeleted = {}
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (adapter.items.isEmpty()) {
            inflater.inflate(R.menu.menu_set_overview, menu)
        } else {
            inflater.inflate(R.menu.menu_set_overview_sets_existing, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val ctx = context
        if (ctx != null) {
            when (item.itemId) {
                R.id.action_add -> showAddEditSetDialog(ctx)
                R.id.action_select_all -> adapter.activateAllItems()
                R.id.action_more -> {
                    adapter.deactiveAllItems()
                    goTo(MoreKey())
                }
                R.id.action_settings -> {
                    adapter.deactiveAllItems()
                    goTo(SettingsKey())
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

        //region hide NoItemsText
        txtNoItems.gone()
        //endregion
        //region select adapter item
        itemRoot.alpha = 0F
        itemRoot.visible()
        itemRoot.animate()
            .alpha(1F)
            .setDuration(300)
            .resetListener()
            .start()

        CoroutineScope(Dispatchers.IO).launch {
            delay(600)
            CoroutineScope(Dispatchers.Main).launch {
                viewSelected.visible()
            }
        }
        //endregion
        //region show PlayButton and PlayInverseButton at elevation 0
        listOf(btnPlay, btnPlayInverse).forEach {
            it.elevation = 0F
            it.alpha = 0F
            it.visible()
            it.animate()
                .alpha(1F)
                .setStartDelay(300)
                .setDuration(300)
                .resetListener()
                .start()
        }
        //endregion
        //region show TutorialBackground
        vTutorialBack.alpha = 0F
        vTutorialBack.visible()
        vTutorialBack.animate()
            .alpha(0.8F)
            .setStartDelay(300)
            .setDuration(300)
            .resetListener()
            .start()
        //endregion
        //region show Initial Tutorial Text
        listOf(
            txtTutorialSelect,
            txtTutorialOk
        ).forEach {
            it.alpha = 0F
            it.visible()
            it.animate()
                .alpha(1F)
                .setStartDelay(700)
                .setDuration(300)
                .resetListener()
                .start()
        }
        //endregion
    }


    private fun goToSet(setId: Int) {
        shouldShowButtons = false
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
                    .resetListener()
                    .start()
                it.animate()
                    .z(8F)
                    .setDuration(300)
                    .setStartDelay(300)
                    .resetListener()
                    .start()
            }
        } else {
            listOf(btnPlay, btnPlayInverse).forEach {
                it.animate()
                    .alpha(0F)
                    .setDuration(300)
                    .z(0F)
                    .setListener(
                        onAnimationEnd = {
                            it.gone()
                        }
                    )
                    .start()
            }
        }
    }

    private fun nextTutorialStep() {
        tutorialStep = when (tutorialStep) {
            TutorialStep.SHOW_MULTISELECT -> {

                //region lift PlayButton
                btnPlay.z = 0F
                btnPlay.animate()
                    .z(8F)
                    .setDuration(300)
                    .resetListener()
                    .start()
                //endregion
                //region hide TutorialSelect
                txtTutorialSelect.animate()
                    .alpha(0F)
                    .setDuration(300)
                    .setListener(
                        onAnimationEnd = {
                            txtTutorialSelect.invisible()
                        }
                    )
                    .start()
                //endregion
                //region show TutorialPlay
                txtTutorialPlay.alpha = 0F
                txtTutorialPlay.visible()
                txtTutorialPlay.animate()
                    .alpha(1F)
                    .setDuration(300)
                    .setStartDelay(400)
                    .resetListener()
                    .start()
                //endregion

                TutorialStep.SHOW_PLAY_NORMAL
            }
            TutorialStep.SHOW_PLAY_NORMAL -> {

                //region drop PlayButton
                btnPlay.animate()
                    .z(0F)
                    .setDuration(300)
                    .resetListener()
                    .start()
                //endregion
                //region lift PlayInverseButton
                btnPlayInverse.z = 0F
                btnPlayInverse.animate()
                    .z(8F)
                    .setDuration(300)
                    .resetListener()
                    .start()
                //endregion
                //region hide TutorialPlay
                txtTutorialPlay.animate()
                    .alpha(0F)
                    .setDuration(300)
                    .setListener(
                        onAnimationEnd = {
                            txtTutorialPlay.invisible()
                        }
                    )
                    .start()
                //endregion
                //region show TutorialPlayInverse
                txtTutorialPlayInverse.alpha = 0F
                txtTutorialPlayInverse.visible()
                txtTutorialPlayInverse.animate()
                    .alpha(1F)
                    .setDuration(300)
                    .setStartDelay(400)
                    .resetListener()
                    .start()
                //endregion

                TutorialStep.SHOW_PLAY_INVERTED
            }
            TutorialStep.SHOW_PLAY_INVERTED, TutorialStep.DONE -> {

                //region drop PlayInverseButton
                btnPlayInverse.animate()
                    .z(0F)
                    .setDuration(300)
                    .resetListener()
                    .start()
                //endregion
                //region hide Everything
                listOf(
                    btnPlay,
                    btnPlayInverse,
                    vTutorialBack,
                    txtTutorialOk,
                    txtTutorialPlayInverse
                ).forEach {
                    it.animate()
                        .alpha(0F)
                        .setStartDelay(300)
                        .setDuration(300)
                        .setListener(
                            onAnimationEnd = {
                                it.gone()
                            }
                        )
                        .start()
                }
                adapter.deactiveAllItems()
                //endregion

                TutorialStep.DONE
            }
        }
    }

    private fun playSets(flipCards: Boolean) {
        goTo(PlayKey(adapter.getSelectedItems().map { it.id }, flipCards))
        adapter.deactiveAllItems()
    }

    private class Adapter(onSomethingSelected: (Boolean) -> Unit, onClick: (Set) -> Unit) :
        BaseAdapter<Set>(onSomethingSelected = onSomethingSelected, onClick = onClick) {

        fun selectFirst() {
            if (items.isNotEmpty()) {
                activeItems.add(0)
                notifyItemChanged(0)
            }
        }

        override fun getItemLayoutId(viewType: Int): Int = R.layout.listitem_set

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            with(holder.itemView) {
                val txtSetName: TextView = findViewById(R.id.txtSetName)
                val viewSelected: View = findViewById(R.id.viewSelected)
                txtSetName.text = items[holder.adapterPosition].name

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
        object SHOW_MULTISELECT : TutorialStep()
        object SHOW_PLAY_NORMAL : TutorialStep()
        object SHOW_PLAY_INVERTED : TutorialStep()
        object DONE : TutorialStep()
    }
}