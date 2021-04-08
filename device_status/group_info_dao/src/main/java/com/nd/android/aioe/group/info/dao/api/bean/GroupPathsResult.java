package com.nd.android.aioe.group.info.dao.api.bean;

import java.util.List;

public class GroupPathsResult {
    private int errcode;
    private List<GroupPathInfo> result;

    public List<GroupPathInfo> getResult() {
        return result;
    }

    public int getErrcode() {
        return errcode;
    }

}
