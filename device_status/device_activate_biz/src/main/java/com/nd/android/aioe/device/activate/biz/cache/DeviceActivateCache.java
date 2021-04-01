package com.nd.android.aioe.device.activate.biz.cache;

import android.text.TextUtils;

import com.nd.android.adhoc.control.define.IControl_OrgId;
import com.nd.android.aioe.device.info.util.DeviceInfoHelper;
import com.nd.android.mdm.basic.ControlFactory;

public class DeviceActivateCache {

    private static String sOrgId;

    public static String getOrgId() {
        if (TextUtils.isEmpty(sOrgId)) {
            sOrgId = getOrgIdThroughControl();
        }
        return sOrgId;
    }


    private static String getOrgIdThroughControl() {
        IControl_OrgId control = ControlFactory.getInstance().getControl
                (IControl_OrgId.class);
        try {
            if (control != null) {
                return control.getOrgId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
