package com.nd.android.aioe.group.info.dao.api.bean;

import java.util.List;

public class IpLocationGroupResult {

    private int errcode = 0;
    private String msg = "";

    private List<SubGroupNodeInfoExt> list;

    public IpLocationGroupResult() {
        super();
    }

    public List<SubGroupNodeInfoExt> getList() {
        return list;
    }

    public String getMsg() {
        return msg;
    }

    public int getErrcode() {
        return errcode;
    }
}
