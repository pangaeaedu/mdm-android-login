package com.nd.android.adhoc.login.basicService.data.http;

public class SearchSchoolNodeByGroupCode {
    private String groupcode = "";

    private String groupname = "";

    private GroupNodeInfo district = null;

    private GroupNodeInfo governorate = null;

    public GroupNodeInfo getDistrict(){
        return district;
    }

    public GroupNodeInfo getGovernorate(){
        return governorate;
    }

    public String getGroupcode() {
        return groupcode;
    }

    public String getGroupname() {
        return groupname;
    }
}
