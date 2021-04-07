package com.nd.android.aioe.group.info.dao.api.bean;

import com.google.gson.annotations.SerializedName;

public class SubGroupNodeInfo {
    @SerializedName("groupcode")
    private String groupcode = "";

    @SerializedName("groupname")
    private String groupname = "";

    @SerializedName("district")
    private String district = null;

    @SerializedName("governorate")
    private String governorate = null;

    public String getDistrict() {
        return district;
    }

    public String getGovernorate() {
        return governorate;
    }

    public String getGroupcode() {
        return groupcode;
    }


    public String getGroupname() {
        return groupname;
    }

}