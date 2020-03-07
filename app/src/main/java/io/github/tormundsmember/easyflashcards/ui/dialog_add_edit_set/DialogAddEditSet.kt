package io.github.tormundsmember.easyflashcards.ui.dialog_add_edit_set

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.dialog_delete_set.DialogDeleteSet
import io.github.tormundsmember.easyflashcards.ui.set_overview.model.Set
import io.github.tormundsmember.easyflashcards.ui.util.Action
import io.github.tormundsmember.easyflashcards.ui.util.gone
import io.github.tormundsmember.easyflashcards.ui.util.putCursorInTextview
import io.github.tormundsmember.easyflashcards.ui.util.visible

class DialogAddEditSet private constructor(
    private val dialog: AlertDialog,
    private var viewHolder: ViewHolder?,
    private val set: Set?,
    private val onDeleted: Action,
    private val onSetAdded: (Int) -> Unit
) {


    companion object {

        @SuppressLint("InflateParams")
        fun show(context: Context, setId: Int? = null, onDeleted: Action, onSetAdded: (Int) -> Unit): DialogAddEditSet {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_set, null, false)

            val set = if (setId != null) {
                Dependencies.database.getSetById(setId)
            } else {
                null
            }

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()

            return DialogAddEditSet(dialog, ViewHolder(view), set, onDeleted, onSetAdded).also {
                dialog.show()
            }
        }
    }

    private var txtListener: TextWatcher? = null

    init {
        viewHolder?.run {
            val set = set
            txtOriginalTerm.setText(set?.name ?: "")
            btnSaveTerm.isEnabled = !set?.name?.trim().isNullOrEmpty()

            btnSaveTerm.setOnClickListener {
                addOrSaveTerm(txtOriginalTerm.text.toString())
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
            txtOriginalTerm.putCursorInTextview(true)
            txtOriginalTerm.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addOrSaveTerm(txtOriginalTerm.text.toString())
                }
                true
            }

            if (set != null) {
                btnDelete.visible()
                btnDelete.setOnClickListener {
                    DialogDeleteSet.show(it.context, set.id) {
                        onDeleted()
                        dismiss()
                    }
                }
            } else {
                btnDelete.gone()
            }
            txtListener = txtOriginalTerm.addTextChangedListener(onTextChanged = { text, _, _, _ ->
                btnSaveTerm.isEnabled = !text?.trim().isNullOrEmpty()
            })
        }
    }

    private fun dismiss() {
        txtListener?.let {
            viewHolder?.txtOriginalTerm?.removeTextChangedListener(txtListener)
        }
        viewHolder = null
        txtListener = null
        dialog.dismiss()
    }

    private fun addOrSaveTerm(title: String) {
        if (title.isNotBlank() && title.isNotEmpty()) {
            val newSet = set?.copy(name = title) ?: Set(
                Dependencies.database.getHighestSetId(),
                title
            )
            if (set != null) {
                Dependencies.database.updateSet(newSet)
            } else {
                Dependencies.database.addSet(newSet)
            }
            onSetAdded(newSet.id)
            dismiss()
        }
    }


    private class ViewHolder(view: View) {
        val txtOriginalTerm: AppCompatEditText = view.findViewById(R.id.txtOriginalTerm)
        val btnSaveTerm: AppCompatButton = view.findViewById(R.id.btnSaveTerm)
        val btnCancel: AppCompatButton = view.findViewById(R.id.btnCancel)
        val btnDelete: View = view.findViewById(R.id.btnDelete)
    }
}