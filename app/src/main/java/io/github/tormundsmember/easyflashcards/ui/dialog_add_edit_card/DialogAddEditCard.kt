package io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_card

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.dialog_delete_card.DialogDeleteCard
import io.github.tormundsmember.easyflashcards.ui.set.model.Card
import io.github.tormundsmember.easyflashcards.ui.set.model.RehearsalInterval
import io.github.tormundsmember.easyflashcards.ui.util.gone
import io.github.tormundsmember.easyflashcards.ui.util.isNotEmptyOrBlank
import io.github.tormundsmember.easyflashcards.ui.util.putCursorInTextview
import io.github.tormundsmember.easyflashcards.ui.util.visible

class DialogAddEditCard private constructor(
    private val dialog: AlertDialog,
    private val setId: Int,
    private val viewHolder: ViewHolder,
    private val card: Card?
) {

    companion object {

        @SuppressLint("InflateParams")
        fun show(context: Context, setId: Int, card: Card? = null): DialogAddEditCard {

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_card, null, false)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

            return DialogAddEditCard(
                dialog = dialog,
                setId = setId,
                viewHolder = ViewHolder(view),
                card = card
            ).also {
                dialog.show()
            }
        }
    }

    init {
        with(viewHolder) {
            val card = card
            txtOriginalTerm.setText(card?.frontText)
            txtRevealedTerm.setText(card?.backText)
            btnSaveTerm.setOnClickListener {
                saveCard(txtOriginalTerm.text.toString(), txtRevealedTerm.text.toString())
            }
            btnCancel.setOnClickListener {
                dismiss()
            }

            txtOriginalTerm.putCursorInTextview(true)

            txtRevealedTerm.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveCard(txtOriginalTerm.text.toString(), txtRevealedTerm.text.toString())
                }
                true
            }

            if (card != null) {
                btnDelete.visible()
                btnDelete.setOnClickListener {
                    DialogDeleteCard.show(it.context, card) {
                        dismiss()
                    }
                }
            } else {
                btnDelete.gone()
            }
        }
    }

    private fun dismiss() {
        dialog.dismiss()
    }

    private fun saveCard(originalTerm: String, revealedTerm: String) {
        if (originalTerm.isNotEmptyOrBlank() && revealedTerm.isNotEmptyOrBlank()) {
            val card = card?.copy(frontText = originalTerm, backText = revealedTerm) ?: Card(
                id = Dependencies.database.getHighestCardIdForSet(setId),
                frontText = originalTerm,
                backText = revealedTerm,
                currentInterval = RehearsalInterval.STAGE_1.getInterval(),
                nextRecheck = System.currentTimeMillis(),
                setId = setId,
                checkCount = 0,
                positiveCheckCount = 0
            )

            Dependencies.database.addOrUpdateCard(card)
            dismiss()
        }
    }


    private class ViewHolder(view: View) {
        val txtOriginalTerm: AppCompatEditText = view.findViewById(R.id.txtOriginalTerm)
        val txtRevealedTerm: AppCompatEditText = view.findViewById(R.id.txtRevealedTerm)
        val btnSaveTerm: AppCompatButton = view.findViewById(R.id.btnSaveTerm)
        val btnCancel: AppCompatButton = view.findViewById(R.id.btnCancel)
        val btnDelete: View = view.findViewById(R.id.btnDelete)


    }
}