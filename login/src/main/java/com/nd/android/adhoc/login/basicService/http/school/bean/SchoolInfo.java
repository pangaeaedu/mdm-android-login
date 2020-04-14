package com.nd.android.adhoc.login.basicService.http.school.bean;

public class SchoolInfo {
    private String groupcode;
    private String groupname;
    private String governorate;
    private String district;


    // 根据 OMO 要求新增
    // https://pms.101.com/index.php?m=bug&f=view&bugID=493135
    // https://pms.101.com/index.php?m=bug&f=view&bugID=493071
    // -- by hyk 20200414
    private String distance;
    private String school_name;

    public String getGroupcode() {
        return groupcode;
    }

    public String getGroupname() {
        return groupname;
    }

    public String getGovernorate() {
        return governorate;
    }

    public String getDistrict() {
        return district;
    }

    public String getDistance() {
        return distance;
    }

    public String getSchool_name() {
        return school_name;
    }
}
