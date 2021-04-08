package com.nd.android.aioe.group.info.dao.api.bean;

public class CheckGroupExitResult {

    private int errorcode; //0=找到（找到才有以下值），-2=没找到，-1=异常

    private String school_name;//学校编码或者名称

    private String groupcode = "";//组的groupcode

    private String groupname = "";//组的name

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
