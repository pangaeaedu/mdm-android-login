package com.nd.android.adhoc.login.ui.widget;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Administrator on 2017/9/27 0027.
 */

public class TabletUtil {

    public static boolean isTablet(Context context) {
        int iScreenLayout = context.getResources().getConfiguration().screenLayout;
        int iScreenLayoutMask = Configuration.SCREENLAYOUT_SIZE_MASK;
        int iScreenLayoutValue = iScreenLayout & iScreenLayoutMask;
        return (iScreenLayoutValue) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
