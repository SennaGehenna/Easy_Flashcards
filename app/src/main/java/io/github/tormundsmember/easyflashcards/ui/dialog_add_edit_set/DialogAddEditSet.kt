package io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_set

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.util.putCursorInTextview

class DialogAddEditSet private constructor(
    private val dialog: AlertDialog,
    viewHolder: ViewHolder,
    private val set: Set?
) {


    companion object {

        @SuppressLint("InflateParams")
        fun show(context: Context, setId: Int? = null): DialogAddEditSet {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_set, null, false)

            val set = if (setId != null) {
                Dependencies.database.getSetById(setId)
            } else {
                null
            }

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

            return DialogAddEditSet(dialog, ViewHolder(view), set).also {
                dialog.show()
            }
        }
    }

    init {
        with(viewHolder) {
            txtOriginalTerm.setText(set?.name ?: "")

            btnSaveTerm.setOnClickListener {
                addOrSaveTerm(txtOriginalTerm.text.toString())
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
            txtOriginalTerm.putCursorInTextview()
        }
    }

    private fun dismiss() {
        dialog.dismiss()
    }

    private fun addOrSaveTerm(title: String) {
        if (title.isNotBlank() && title.isNotEmpty()) {
            val newSet = set?.copy(name = title) ?: Set(
                Dependencies.database.getHighestSetId(),
                title
            )
            Dependencies.database.addOrUpdateSet(newSet)
            dismiss()
        }
    }


    private class ViewHolder(view: View) {
        val txtOriginalTerm: AppCompatEditText = view.findViewById(R.id.txtOriginalTerm)
        val btnSaveTerm: AppCompatButton = view.findViewById(R.id.btnSaveTerm)
        val btnCancel: AppCompatButton = view.findViewById(R.id.btnCancel)
    }
}