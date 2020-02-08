package io.github.tormundsmember.easyflashcards.ui.base_ui;

import androidx.annotation.AnimRes;

public class AnimationSet {

    @AnimRes
    private final int enterAnimation;
    @AnimRes
    private final int exitAnimation;

    public AnimationSet(int enterAnimation, int exitAnimation) {
        this.enterAnimation = enterAnimation;
        this.exitAnimation = exitAnimation;
    }

    public int getEnterAnimation() {
        return enterAnimation;
    }

    public int getExitAnimation() {
        return exitAnimation;
    }
}
