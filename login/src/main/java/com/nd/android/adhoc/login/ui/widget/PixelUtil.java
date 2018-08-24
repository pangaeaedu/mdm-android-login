package com.nd.android.adhoc.login.ui.widget;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2016/12/31 0031.
 */

public class PixelUtil {

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String readTextFromSDcard(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            builder.append(str);
            builder.append("\n");
        }
        return builder.toString();
    }
}
