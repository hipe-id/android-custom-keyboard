package id.hipe.utils;

import android.view.animation.TranslateAnimation;

/**
 * Created by cak_upik on 5/12/18.
 */

public class CustomAnimation {

    public static TranslateAnimation slideToLeft(int width, long duration) {
        TranslateAnimation animator = new TranslateAnimation(0, -width, 0, 0);
        animator.setDuration(duration);
        animator.setFillAfter(true);

        return animator;
    }

    public static TranslateAnimation slideToRight(int width, long duration) {
        TranslateAnimation animator = new TranslateAnimation(0, width, 0, 0);
        animator.setDuration(duration);
        animator.setFillAfter(true);

        return animator;
    }

    public static TranslateAnimation slideFromLeft(int width, long duration) {
        TranslateAnimation animator = new TranslateAnimation(-width, 0, 0, 0);
        animator.setDuration(duration);
        animator.setFillAfter(true);

        return animator;
    }

    public static TranslateAnimation slideFromRight(int width, long duration) {
        TranslateAnimation animator = new TranslateAnimation(width, 0, 0, 0);
        animator.setDuration(duration);
        animator.setFillAfter(true);

        return animator;
    }
}
