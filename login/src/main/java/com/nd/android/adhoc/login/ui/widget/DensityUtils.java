package com.nd.android.adhoc.login.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by richsjeson on 2018/2/10.
 *
 * @author richsjeson
 *         计算屏幕尺寸及屏幕尺寸转换工具
 */

public class DensityUtils {

    /**
     * px转dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(@NonNull Context pContext, float pxValue) {
        float density = pContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * px转sp
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(@NonNull Context pContext, float pxValue) {
        float density = pContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 计算尺寸
     *
     * @param scaled 比例值
     * @return
     */
    public static float calDensityWidth(@NonNull Context pContext, float scaled) {
        int widthPixels = pContext.getResources().getDisplayMetrics().widthPixels;
        return widthPixels * scaled;
    }

    /**
     * 计算尺寸
     *
     * @param scaled 比例值
     * @return
     */
    public static float calDensityHeight(@NonNull Context pContext,float scaled) {
        int heightPixels = pContext.getResources().getDisplayMetrics().heightPixels;
        return heightPixels * scaled;
    }
}
