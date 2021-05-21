package com.nd.android.adhoc.login.ui.widget.edit.strategy.style;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.core.graphics.drawable.DrawableCompat;
import android.widget.EditText;

/**
 * Created by richsjeson on 2018/2/13.
 * @author 使用策略模式抽离
 */

public abstract class BaseStyle {
    /**
     * 背景渲染色 -- 存储的当前color值
     */
    protected int color;
    /**
     * 控件属性
     */
    protected EditText mView=null;

    protected float scale=1f;

    protected TypedArray mTypedArray;

    public void  initStyleProperties(EditText view, float scale, TypedArray typedArray) {
        this.mView=view;
        this.scale=scale;
        this.mTypedArray=typedArray;
    }

    /**
     * @设置比例
     * @param scale
     */
    public void setScale(float scale){
        this.scale=scale;
    }

    /**
     * 焦点对焦时的背景框策略
     */
    public abstract void onFoucsBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId);

    public abstract void onErrorBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId);

    public abstract void onUnFocusBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId);

    public abstract void onDraw(Canvas canvas);

    /**
     * 通过渲染处理drawable
     *
     * @param drawable
     * @param color 外界传入的颜色值
     * @return
     */
    public Drawable renderDrawable(Drawable drawable, int color) {
        Drawable renderDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintList(renderDrawable, ColorStateList.valueOf(color));
        return renderDrawable;
    }

    public void release() {
        mView = null;
        if (mTypedArray != null) {
            mTypedArray.recycle();
            mTypedArray = null;
        }
    }
}
