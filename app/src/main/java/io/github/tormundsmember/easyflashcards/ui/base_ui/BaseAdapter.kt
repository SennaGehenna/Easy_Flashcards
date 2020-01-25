package io.github.tormundsmember.easyflashcards.ui.base_ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R

abstract class BaseAdapter<T>(val onSomethingSelected: ((Boolean) -> Unit) = {}, val onClick: (T) -> Unit = {}) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<T> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    protected val activeItems = mutableListOf<Int>()
    abstract fun getItemLayoutId(viewType: Int): Int

    fun getSelectedItems(): List<T> {
        return items.filterIndexed { index, _ -> index in activeItems }
    }

    protected val RecyclerView.ViewHolder.longClick: (View) -> Boolean
        get() {
            return {
                val adapterPosition = adapterPosition
                if (activeItems.contains(adapterPosition)) {
                    activeItems -= adapterPosition
                } else {
                    activeItems += adapterPosition
                }
                onSomethingSelected(activeItems.isNotEmpty())
                notifyItemChanged(adapterPosition)
                true
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                getItemLayoutId(viewType),
                parent,
                false
            )
        ) {}
    }

    override fun getItemCount(): Int = items.size

    fun deactiveAllItems() {
        activeItems.clear()
        notifyDataSetChanged()
    }

    fun Boolean.mapActiveColor() = if (this) R.color.colorAccent else R.color.transparent
    fun Boolean?.mapToVisibility() = if (this == true) View.VISIBLE else View.GONE
}