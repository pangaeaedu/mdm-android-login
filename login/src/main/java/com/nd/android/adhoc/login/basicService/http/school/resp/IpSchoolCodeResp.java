package com.nd.android.adhoc.login.basicService.http.school.resp;

import com.nd.android.adhoc.login.basicService.http.school.bean.SchoolInfo;

public class IpSchoolCodeResp extends BaseSchoolCodeResp {
    private SchoolInfo group;

    public IpSchoolCodeResp() {
        super();
    }

    public SchoolInfo getGroup(){
        return group;
    }
}
