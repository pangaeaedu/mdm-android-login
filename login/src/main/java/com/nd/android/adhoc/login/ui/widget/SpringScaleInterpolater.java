package com.nd.android.adhoc.login.ui.widget;

import android.view.animation.Interpolator;

/**
 * Created by richsjeson on 2018/2/13.
 * @author zyb 创建一个插值器
 */

public class SpringScaleInterpolater implements Interpolator {

    private float factor;

    public SpringScaleInterpolater(float factor){
        this.factor=factor;
    }
    @Override
    public float getInterpolation(float v) {
        return (float)(Math.pow(2,-10*v)*(Math.sin(v-factor/4)
        *(2* Math.PI)/factor)+1);
    }
}
