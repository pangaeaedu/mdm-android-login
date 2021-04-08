package com.nd.android.aioe.group.info.dao.api.bean;

public class SubGroupNodeInfoExt extends SubGroupNodeInfo {

    // 根据 OMO 要求新增
    // https://pms.101.com/index.php?m=bug&f=view&bugID=493135
    // https://pms.101.com/index.php?m=bug&f=view&bugID=493071
    // -- by hyk 20200414
    private String distance;
    private String school_name;

    public String getDistance() {
        return distance;
    }

    public String getSchool_name() {
        return school_name;
    }
}
