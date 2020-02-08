package io.github.tormundsmember.easyflashcards.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.util.animateGone
import io.github.tormundsmember.easyflashcards.ui.util.animateVisible

class PlayButtonsLayout(context: Context, attributeSet: AttributeSet?) : ConstraintLayout(context, attributeSet) {

    init {
        inflate(
            context,
            R.layout.layout_play_buttons, this
        )
    }

    val btnNegative: AppCompatImageButton = findViewById(R.id.btnNegative)
    val btnPositive: AppCompatImageButton = findViewById(R.id.btnPositive)
    val btnFlip: AppCompatButton = findViewById(R.id.btnFlip)
    val btnUndo: AppCompatImageButton = findViewById(R.id.btnUndo)


    fun showFeedbackButtons() {
        btnNegative.animateVisible()
        btnPositive.animateVisible()
        btnFlip.animateGone()
    }

    fun showFlipButton() {
        btnNegative.animateGone()
        btnPositive.animateGone()
        btnFlip.animateVisible()
    }

    fun showUndoButton() {
        btnUndo.animateVisible()
    }

    fun hideUndoButton() {
        btnUndo.animateGone()
    }
}