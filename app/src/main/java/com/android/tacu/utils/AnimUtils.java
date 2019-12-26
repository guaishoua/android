package com.android.tacu.utils;

import android.view.View;
import android.view.animation.ScaleAnimation;


/**
 * Created by jiazhen on 2019/4/23.
 */
public class AnimUtils {
    /**
     * 给View添加放大缩小的动画 持续1秒
     */
    public static void startTextViewAnim(View view) {
        view.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //设置动画持续时长
        scaleAnimation.setDuration(800);
        scaleAnimation.setFillAfter(false);
        view.startAnimation(scaleAnimation);
    }

    public static void startTextViewBottomRightAnim(View view) {
        view.setVisibility(View.VISIBLE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, ScaleAnimation.RELATIVE_TO_SELF, 1f, ScaleAnimation.RELATIVE_TO_SELF, 1f);
        //设置动画持续时长
        scaleAnimation.setDuration(800);
        scaleAnimation.setFillAfter(false);
        view.startAnimation(scaleAnimation);
    }
}
