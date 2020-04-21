package com.nd.android.adhoc.login.basicService.http.school.bean;

import com.nd.android.adhoc.login.basicService.data.http.GroupNodeInfo;

/**
 * Created by HuangYK on 2020/3/17.
 */

public class CheckSchoolExitResult {

    private int errcode;

    private String groupcode;

    private String groupname;

    private String school_name;

    private GroupNodeInfo district;

    private GroupNodeInfo governorate;

    public int getErrcode() {
        return errcode;
    }

    public String getGroupcode() {
        return groupcode;
    }

    public String getGroupname() {
        return groupname;
    }

    public String getSchool_name() {
        return school_name;
    }

    public GroupNodeInfo getDistrict() {
        return district;
    }

    public GroupNodeInfo getGovernorate() {
        return governorate;
    }

}
