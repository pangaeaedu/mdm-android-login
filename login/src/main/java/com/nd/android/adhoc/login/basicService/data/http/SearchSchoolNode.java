package com.nd.android.adhoc.login.basicService.data.http;

public class SearchSchoolNode {
    private String schoolid = "";

    private GroupNodeInfo district = null;

    private GroupNodeInfo governorate = null;

    public String getSchoolID(){
        return schoolid;
    }

    public GroupNodeInfo getDistrict(){
        return district;
    }

    public GroupNodeInfo getGovernorate(){
        return governorate;
    }
}
