package io.github.tormundsmember.easyflashcards.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.util.animateGone
import io.github.tormundsmember.easyflashcards.ui.util.animateVisible
import io.github.tormundsmember.easyflashcards.ui.util.gone
import io.github.tormundsmember.easyflashcards.ui.util.visible

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
}