package io.github.tormundsmember.easyflashcards.ui.dialog_delete_card

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.set.model.Card

class DialogDeleteCard private constructor(
    private val dialog: AlertDialog,
    viewHolder: ViewHolder,
    private val card: Card,
    private val onDelete: () -> Unit
) {


    companion object {

        @SuppressLint("InflateParams")
        fun show(context: Context, card: Card, onDelete: () -> Unit): DialogDeleteCard {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_cards, null, false)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

            return DialogDeleteCard(dialog, ViewHolder(view), card, onDelete).also {
                dialog.show()
            }
        }
    }

    init {
        with(viewHolder) {
            btnDeleteTerm.setOnClickListener {
                deleteTerm()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }

        }
    }

    private fun dismiss() {
        dialog.dismiss()
    }

    private fun deleteTerm() {
        Dependencies.database.deleteCard(card)
        dismiss()
        onDelete()
    }


    private class ViewHolder(view: View) {
        val btnDeleteTerm: AppCompatButton = view.findViewById(R.id.btnDeleteTerm)
        val btnCancel: AppCompatButton = view.findViewById(R.id.btnCancel)
    }
}