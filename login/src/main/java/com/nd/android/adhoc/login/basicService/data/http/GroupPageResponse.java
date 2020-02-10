package com.nd.android.adhoc.login.basicService.data.http;

import java.util.List;

/**
 * Created by Administrator on 2019/10/18 0018.
 */

public class GroupPageResponse {
    private int iTotalDisplayRecords;
    private int iTotalRecords;
    private List<GroupPageNode> aaData;

    public GroupPageResponse(){

    }

    public int getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(int pITotalDisplayRecords) {
        iTotalDisplayRecords = pITotalDisplayRecords;
    }

    public int getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(int pITotalRecords) {
        iTotalRecords = pITotalRecords;
    }

    public List<GroupPageNode> getAaData() {
        return aaData;
    }

    public void setAaData(List<GroupPageNode> pAaData) {
        aaData = pAaData;
    }
}
