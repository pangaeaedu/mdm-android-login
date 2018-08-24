package com.nd.android.adhoc.login.ui.widget.edit.strategy.style;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by richsjeson on 2018/2/13.
 *
 * @author 背景框 切换色值
 */

public class BoxStyle extends BaseStyle {

    /**
     * 渲染的drawable 对焦
     */
    private Drawable mLeftFoucsRenderDrawable;
    private Drawable mFocusBackgroundDrawable;

    private Drawable mWaringBackgroundDrawable;

    @Override
    @SuppressLint("ResourceType")
    public void onFoucsBackground(int color, int leftDrawableResId, int rightDrawableResId, int backgroundDrawableResId) {
        if(mView == null || mTypedArray == null){
            return;
        }

        if (color != -1) {
            if ((mLeftFoucsRenderDrawable == null) && (leftDrawableResId > -1)) {
                Drawable leftDrawable = this.mTypedArray.getDrawable(leftDrawableResId);
                mLeftFoucsRenderDrawable = renderDrawable(leftDrawable, color);
            } else {
                return;
            }

            mLeftFoucsRenderDrawable.setBounds(0, 0, (int) (mLeftFoucsRenderDrawable.getIntrinsicWidth() * this.scale), (int) (mLeftFoucsRenderDrawable.getIntrinsicHeight() * this.scale));

            Drawable rightDrawable = this.mTypedArray.getDrawable(rightDrawableResId);
            ;

            mView.setCompoundDrawables(mLeftFoucsRenderDrawable, null, rightDrawable, null);

            if ((mFocusBackgroundDrawable == null) && (backgroundDrawableResId > -1)) {
                Drawable backgroundDrawable = this.mTypedArray.getDrawable(backgroundDrawableResId);
                mFocusBackgroundDrawable = renderDrawable(backgroundDrawable, color);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mView.setBackground(mFocusBackgroundDrawable);
            } else {
                mView.setBackgroundDrawable(mFocusBackgroundDrawable);
            }
        }
    }

    @Override
    @SuppressLint("ResourceType")
    public void onErrorBackground(int color, int leftDrawableResId, int rightDrawableResId, int backgroundDrawableResId) {
        if(mView == null || mTypedArray == null){
            return;
        }
        if (color != -1) {
            if ((mWaringBackgroundDrawable == null) && (backgroundDrawableResId > -1)) {
                Drawable backgroundDrawable = this.mTypedArray.getDrawable(backgroundDrawableResId);
                mWaringBackgroundDrawable = renderDrawable(backgroundDrawable, color);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mView.setBackground(mWaringBackgroundDrawable);
            } else {
                mView.setBackgroundDrawable(mWaringBackgroundDrawable);
            }
        }
    }

    @Override
    @SuppressLint("ResourceType")
    public void onUnFocusBackground(int color, int leftDrawableResId, int rightDrawableResId, int backgroundDrawableResId) {
        if(mView == null || mTypedArray == null){
            return;
        }

        this.color = color;
        Drawable leftDrawable = this.mTypedArray.getDrawable(leftDrawableResId);
        Drawable rightDrawable = this.mTypedArray.getDrawable(rightDrawableResId);
        if (rightDrawable != null) {
            rightDrawable.setBounds(0, 0, (int) (leftDrawable.getIntrinsicWidth() * this.scale), (int) (leftDrawable.getIntrinsicHeight() * this.scale));
        }
        if (leftDrawable != null) {
            leftDrawable.setBounds(0, 0, (int) (leftDrawable.getIntrinsicWidth() * this.scale), (int) (leftDrawable.getIntrinsicHeight() * this.scale));
        }
        Drawable backgroundDrawable = this.mTypedArray.getDrawable(backgroundDrawableResId);
        mView.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mView.setBackground(backgroundDrawable);
        } else {
            mView.setBackgroundDrawable(backgroundDrawable);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public void release() {
        super.release();
        mFocusBackgroundDrawable = null;
        mLeftFoucsRenderDrawable = null;
        mWaringBackgroundDrawable = null;
    }
}
