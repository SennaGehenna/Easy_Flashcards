package io.github.tormundsmember.easyflashcards.ui.search

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.databinding.ListitemCardBinding
import io.github.tormundsmember.easyflashcards.databinding.ScreenSearchBinding
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseAdapter
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_card.DialogAddEditCard
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.util.Click

class SearchFragment : BaseFragment() {

    override val layoutId: Int
        get() = R.layout.screen_search

    private val searchAdapter = Adapter {
        DialogAddEditCard.show(requireContext(), it.setId, it) {

        }
    }

    private val viewModel: SearchViewModel by lazy {
        @Suppress("RemoveExplicitTypeArguments") //doesn't compile otherwise
        getViewModel<SearchViewModel>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(ScreenSearchBinding.bind(view)) {
            listSearchResults.apply {
                layoutManager = LinearLayoutManager(view.context)
                adapter = searchAdapter
            }
            txtSearch.addTextChangedListener(afterTextChanged = {
                viewModel.search(it?.toString() ?: "")
            })
        }


        viewModel.searchResults.observe {
            searchAdapter.items = it ?: emptyList()
        }
    }

    private class Adapter(onClick: Click<Card>) : BaseAdapter<Card>(onClick = onClick) {

        override fun getItemLayoutId(viewType: Int): Int = R.layout.listitem_card

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            with(ListitemCardBinding.bind(holder.itemView)) {

                txtTranslatedTerm.text = items[holder.adapterPosition].backText
                txtOriginalTerm.text = items[holder.adapterPosition].frontText

                viewSelected.setBackgroundResource((activeItems.contains(position)).mapActiveColor())
                viewSelected.visibility = activeItems.contains(position).mapToVisibility()
                root.setOnClickListener {
                    onClick(items[holder.adapterPosition])
                }
            }
        }

    }
}