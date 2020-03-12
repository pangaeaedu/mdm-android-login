package com.nd.android.adhoc.login.basicService.http.school.resp;

import com.nd.android.adhoc.login.basicService.http.school.bean.LocationSchoolInfo;

public class LocationSchoolCodeResp extends BaseSchoolCodeResp {
    private LocationSchoolInfo group;

    public LocationSchoolCodeResp() {
        super();
    }

    public LocationSchoolInfo getGroup() {
        return group;
    }
}
