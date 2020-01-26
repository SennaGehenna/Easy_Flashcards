package io.github.tormundsmember.easyflashcards.ui.dialog_delete_set

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set

class DialogDeleteSet private constructor(
    private val dialog: AlertDialog,
    viewHolder: ViewHolder,
    private val set: Set,
    private val onDelete: () -> Unit
) {


    companion object {

        @SuppressLint("InflateParams")
        fun show(context: Context, setId: Int, onDelete: () -> Unit): DialogDeleteSet {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_set, null, false)

            val set = Dependencies.database.getSetById(setId)

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

            return DialogDeleteSet(dialog, ViewHolder(view), set, onDelete).also {
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
        Dependencies.database.deleteSet(set)
        dismiss()
        onDelete()
    }


    private class ViewHolder(view: View) {
        val btnDeleteTerm: AppCompatButton = view.findViewById(R.id.btnDeleteTerm)
        val btnCancel: AppCompatButton = view.findViewById(R.id.btnCancel)
    }
}