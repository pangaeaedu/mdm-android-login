package com.nd.android.adhoc.login.basicService.http.school.resp;

import com.nd.android.adhoc.login.basicService.http.school.bean.SchoolInfo;

import java.util.List;

public class IpLocationSchoolCodeResp extends BaseSchoolCodeResp {
    private List<SchoolInfo> list;

    public IpLocationSchoolCodeResp() {
        super();
    }

    public List<SchoolInfo> getList() {
        return list;
    }
}
