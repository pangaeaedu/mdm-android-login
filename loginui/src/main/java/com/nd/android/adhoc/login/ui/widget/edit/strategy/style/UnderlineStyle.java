package com.nd.android.adhoc.login.ui.widget.edit.strategy.style;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;


/**
 * Created by richsjeson on 2018/2/13.
 * @author 显示下划线的样式
 */

public class UnderlineStyle extends BaseStyle {

    private Paint mPaint=null;
    /**
     * 渲染的drawable 对焦
     */
    private Drawable mLeftFoucsRenderDrawable;

    private boolean isUnfocus=false;

    public UnderlineStyle(){
        mPaint=new Paint();
    }

    @Override
    @SuppressLint("ResourceType")
    public void onFoucsBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId) {
        this.color=color;
        isUnfocus=false;
        if(color !=-1){
            if ((mLeftFoucsRenderDrawable == null) && (leftDrawableResId > -1)) {
                Drawable leftDrawable = this.mTypedArray.getDrawable(leftDrawableResId);
                mLeftFoucsRenderDrawable = renderDrawable(leftDrawable, color);
                mLeftFoucsRenderDrawable.setBounds(0,0,(int)(leftDrawable.getIntrinsicWidth()*this.scale),(int)(leftDrawable.getIntrinsicHeight()*this.scale));
            }
            Drawable rightDrawable = this.mTypedArray.getDrawable(rightDrawableResId);
            mView.setCompoundDrawables(mLeftFoucsRenderDrawable, null, rightDrawable, null);
        }
        mView.postInvalidate();
    }

    @Override
    public void onErrorBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId) {
        this.color=color;
        mView.postInvalidate();
    }

    @Override
    @SuppressLint("ResourceType")
    public void onUnFocusBackground(int color, @DrawableRes int leftDrawableResId, @DrawableRes int rightDrawableResId, @DrawableRes int backgroundDrawableResId) {
        this.color=color;
        isUnfocus=true;
        if(color !=-1){
            Drawable leftDrawable=this.mTypedArray.getDrawable(leftDrawableResId);
            leftDrawable.setBounds(0,0,(int)(leftDrawable.getIntrinsicWidth()*this.scale),(int)(leftDrawable.getIntrinsicHeight()*this.scale));
            Drawable rightDrawable=this.mTypedArray.getDrawable(rightDrawableResId);
            mView.setCompoundDrawables(leftDrawable, null, rightDrawable, null);
        }
        mView.postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        //此处需要绘制
        if(isUnfocus){

        }else {
            mPaint.setStrokeWidth(3.0f);
            mPaint.setColor(color);
            canvas.drawLine(0, this.mView.getHeight() - 1, this.mView.getWidth(),
                    this.mView.getHeight() - 1, mPaint);
        }
    }
}
