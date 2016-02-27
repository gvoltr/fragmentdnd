package com.gvoltr.dragndrop;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by stanislavgavrosh on 12/22/15.
 */
public class Animations {
    private static final int ANIMATION_LENGTH = 500;

    public static void sizeUpAnimation(View v){
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(v,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleUp.setDuration(ANIMATION_LENGTH);
        scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleUp.start();
    }

    public static void sizeToNormalAnimation(View v){
        ObjectAnimator scaleToNormal = ObjectAnimator.ofPropertyValuesHolder(v,
                PropertyValuesHolder.ofFloat("scaleX", 1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f));
        scaleToNormal.setDuration(ANIMATION_LENGTH);
        scaleToNormal.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleToNormal.start();
    }
}
