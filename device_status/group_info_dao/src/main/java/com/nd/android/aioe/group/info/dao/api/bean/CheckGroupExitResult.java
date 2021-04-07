package com.nd.android.aioe.group.info.dao.api.bean;

public class CheckGroupExitResult {

    private String groupcode = "";

    private String groupname = "";

    private GroupInfo district = null;

    private GroupInfo governorate = null;

    public GroupInfo getDistrict() {
        return district;
    }

    public GroupInfo getGovernorate() {
        return governorate;
    }

    public String getGroupcode() {
        return groupcode;
    }

    public String getGroupname() {
        return groupname;
    }
}
