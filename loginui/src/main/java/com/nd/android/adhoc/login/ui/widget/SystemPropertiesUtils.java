package com.nd.android.adhoc.login.ui.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;

/**
 * Created by richsjeson on 2018/3/13.
 *
 * @author zyb
 *         平板和手机端适配类。将目前系统中所有的适配参数存放至此
 */

public class SystemPropertiesUtils {

    private volatile static SystemPropertiesUtils sInstance;

    //用户输入时的动画属性；
    public static int PROPERTIES_LOGIN_USERPUT_PANEL_TRANSFORM;

    public static int PROPERTIES_LOGIN_USERINPUT_PROFILE_IMAGE_TRANSORM;

    public static float PROPERTIES_COMMON_DIALOG_WIDTH_SCALE;

    public static float PROPERTIES_COMMON_DIALOG_HEIGHT_SCALE;

    public static float PROPERTIES_CONNECT_DIALOG_WIDTH_SCALE;

    public static float PROPERTIES_CONNECT_DIALOG_HEIGHT_SCALE;

    public static float PROPERTIES_VERSION_DIALOG_WIDTH_SCALE;

    public static float PROPERTIES_VERSION_DIALOG_HEIGHT_SCALE;

    //设置动画的


    private Boolean mIsTablet;


    public static SystemPropertiesUtils getInstance() {
        if (sInstance == null) {
            synchronized (SystemPropertiesUtils.class) {
                if (sInstance == null) {
                    sInstance = new SystemPropertiesUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * //此处参数可在该处进行设定
     * 设置属性
     */
    public void setupProperties(@NonNull Context pContext, SetupCallBack pCallBack) {
        if (mIsTablet != null) {
            pCallBack.isOrientation(
                    mIsTablet ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
                    mIsTablet);
            return;
        }

        if (TabletUtil.isTablet(pContext)) {
            doLandscapeInit();
            pCallBack.isOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, true);
        } else {
            pCallBack.isOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, false);
            doPortraitInit(pContext);
        }
    }

    private void doLandscapeInit() {
        mIsTablet = true;
        PROPERTIES_LOGIN_USERPUT_PANEL_TRANSFORM = -100;
        PROPERTIES_LOGIN_USERINPUT_PROFILE_IMAGE_TRANSORM = 50;
        PROPERTIES_COMMON_DIALOG_WIDTH_SCALE = 0.5f;
        PROPERTIES_COMMON_DIALOG_HEIGHT_SCALE = 0.45f;
        PROPERTIES_CONNECT_DIALOG_WIDTH_SCALE = 0.5f;
        PROPERTIES_CONNECT_DIALOG_HEIGHT_SCALE = 0.45f;
        PROPERTIES_VERSION_DIALOG_WIDTH_SCALE = 0.5f;
        PROPERTIES_VERSION_DIALOG_HEIGHT_SCALE = 0.6f;
    }

    private void doPortraitInit(Context pContext) {
        mIsTablet = false;
        PROPERTIES_COMMON_DIALOG_WIDTH_SCALE = 0.9f;
        PROPERTIES_COMMON_DIALOG_HEIGHT_SCALE = 0.45f;
        PROPERTIES_CONNECT_DIALOG_WIDTH_SCALE = 0.9f;
        PROPERTIES_CONNECT_DIALOG_HEIGHT_SCALE = 0.5f;
        PROPERTIES_VERSION_DIALOG_WIDTH_SCALE = 0.9f;
        PROPERTIES_VERSION_DIALOG_HEIGHT_SCALE = 0.5f;
        PROPERTIES_LOGIN_USERPUT_PANEL_TRANSFORM = PixelUtil.dip2px(pContext, -100.5f);
        PROPERTIES_LOGIN_USERINPUT_PROFILE_IMAGE_TRANSORM = PixelUtil.dip2px(pContext, 60.5f);
    }

    public interface SetupCallBack {
        void isOrientation(int orientation, boolean isLand);
    }
}
