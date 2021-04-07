package com.nd.android.aioe.group.info.dao.api.bean;

/**
 * Created by Administrator on 2019/9/19 0019.
 */

public class SearchGroupPathResult {

    private String schoolid = "";

    private GroupInfo district = null;

    private GroupInfo governorate = null;

    public String getSchoolID() {
        return schoolid;
    }

    public GroupInfo getDistrict() {
        return district;
    }

    public GroupInfo getGovernorate() {
        return governorate;
    }
}
