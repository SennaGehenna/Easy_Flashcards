package io.github.tormundsmember.easyflashcards.ui.duplicate_finder

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseAdapter
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_card.DialogAddEditCard
import io.github.tormundsmember.easyflashcards.ui.set.model.Card

class DuplicateFinderFragment : BaseFragment() {

    override val layoutId: Int = R.layout.screen_duplicate_finder
    private var somethingSelected: Boolean = false

    private val adapter: MyAdapter = MyAdapter(onClick = {
        context?.let { ctx ->
            DialogAddEditCard.show(ctx, it.setId, it)
        }
    }, onSomethingSelected = {
        somethingSelected = it
        activity?.invalidateOptionsMenu()
    })

    lateinit var listDuplicates: RecyclerView
    private val viewModel: DuplicateFinderViewModel by lazy {
        getViewModel<DuplicateFinderViewModel>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listDuplicates = view.findViewById(R.id.listDuplicates)
        listDuplicates.layoutManager = LinearLayoutManager(view.context)
        listDuplicates.adapter = adapter
        adapter.items = viewModel.duplicateCards
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuRes = somethingSelected.let {
            if (it) {
                R.menu.menu_duplicate_finder_cards_selected
            } else {
                R.menu.menu_duplicate_finder
            }
        }
        inflater.inflate(menuRes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private class MyAdapter(onClick: (Card) -> Unit, onSomethingSelected: ((Boolean) -> Unit)) :
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
}