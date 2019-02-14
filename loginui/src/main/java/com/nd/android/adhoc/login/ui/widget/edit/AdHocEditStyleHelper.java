package com.nd.android.adhoc.login.ui.widget.edit;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.DrawableRes;
import android.widget.EditText;

import com.nd.android.adhoc.login.ui.widget.edit.strategy.style.BaseStyle;


/**
 * Created by richsjeson on 2018/2/13.
 * @author zyb  调用类
 */

public class AdHocEditStyleHelper {

    private BaseStyle mStyle=null;
    public AdHocEditStyleHelper(){
    }
    //TODO 此处后面要改为读取当前策略的类名进行反射后初始化
    public void setStyle(BaseStyle style){
        this.mStyle=style;
    }

    public void  initStyleProperties(EditText view, float scale, TypedArray typedArray){
        if(mStyle != null) {
            mStyle.initStyleProperties(view, scale, typedArray);
        }
    }

    public void onFoucsBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId){
        if(mStyle != null) {
            mStyle.onFoucsBackground(color, leftDrawableResId, rightDrawableResId, backgroundDrawableResId);
        }
    }

    public void onErrorBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId){
        if(mStyle != null) {
            mStyle.onErrorBackground(color, leftDrawableResId, rightDrawableResId, backgroundDrawableResId);
        }
    }

    public void onUnFocusBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId){
        if(mStyle != null) {
            mStyle.onUnFocusBackground(color, leftDrawableResId, rightDrawableResId, backgroundDrawableResId);
        }
    }

    public  void onDraw(Canvas canvas){
        if(mStyle != null) {
            mStyle.onDraw(canvas);
        }
    }

    public void release() {
        if (mStyle != null) {
            mStyle.release();
            mStyle = null;
        }
    }

}
