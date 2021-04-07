package com.nd.android.aioe.group.info.dao.api.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/10/18 0018.
 */

public class GroupPageResult {
    private int iTotalDisplayRecords;
    private int iTotalRecords;
    private List<GroupPageNodeInfo> aaData;

    public GroupPageResult() {

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

    public List<GroupPageNodeInfo> getAaData() {
        return aaData;
    }

    public void setAaData(List<GroupPageNodeInfo> pAaData) {
        aaData = pAaData;
    }
}
