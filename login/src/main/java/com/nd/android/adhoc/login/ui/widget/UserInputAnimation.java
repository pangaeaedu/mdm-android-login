package com.nd.android.adhoc.login.ui.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


/**
 * Created by richsjeson on 2018/3/9.
 * 登入页面时，用户输入框的动画效果
 */

public class UserInputAnimation {


    private AnimatorSet mAnimationSet;

    private static final int ANIMATION_DURATION = 500;

    /**
     * 对焦时的动画效果
     *
     * @param pPanel        用户面板
     * @param pProfileImage 用户头像
     */
    public void focus(@NonNull View pPanel, @NonNull View pProfileImage) {
        stop();
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator transflatAnimator = ObjectAnimator.ofFloat(pPanel, "translationY", SystemPropertiesUtils.PROPERTIES_LOGIN_USERPUT_PANEL_TRANSFORM);
        transflatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator transflatAnimatorImage = ObjectAnimator.ofFloat(pProfileImage, "translationY", SystemPropertiesUtils.PROPERTIES_LOGIN_USERINPUT_PROFILE_IMAGE_TRANSORM);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.4f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.4f);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(pProfileImage, scaleX, scaleY);
        scaleAnimator.setInterpolator(new SpringScaleInterpolater(0.4f));
        //启用动画
        animatorSet.play(transflatAnimator).with(transflatAnimatorImage).with(scaleAnimator);
        mAnimationSet = animatorSet.clone();
        //回收当前的animtionSet
        onStart();
    }

    /**
     * 失去焦点时的动画效果
     *
     * @param pPanel        用户面板
     * @param pProfileImage 用户头像
     */
    public void unfocus(@NonNull View pPanel, @NonNull View pProfileImage) {
        stop();
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator transflatAnimator = ObjectAnimator.ofFloat(pPanel, "translationY", 0);
        transflatAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator transflatAnimatorImage = ObjectAnimator.ofFloat(pProfileImage, "translationY", 0);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(pProfileImage, scaleX, scaleY);
        scaleAnimator.setInterpolator(new SpringScaleInterpolater(0.4f));
        //启用动画
        animatorSet.play(transflatAnimator).with(transflatAnimatorImage).with(scaleAnimator);
        mAnimationSet = animatorSet.clone();
        //回收当前的animtionSet
        animatorSet = null;
        onStart();
    }

    /**
     * 销毁时，移除动画
     */
    public void onDestory() {
        if ((mAnimationSet != null) && mAnimationSet.isRunning()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAnimationSet.pause();
            }
            mAnimationSet.cancel();
            mAnimationSet = null;
        }
    }

    /**
     * 启动动画
     */
    private void onStart() {
        mAnimationSet.setDuration(ANIMATION_DURATION);
        mAnimationSet.start();
    }

    /**
     * 停止动画
     */
    private void stop() {
        if (mAnimationSet != null && mAnimationSet.isStarted()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAnimationSet.pause();
            }
            mAnimationSet.cancel();
        }
    }
}
