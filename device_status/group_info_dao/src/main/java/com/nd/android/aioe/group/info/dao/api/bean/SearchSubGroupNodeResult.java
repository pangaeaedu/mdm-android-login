package com.nd.android.aioe.group.info.dao.api.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchSubGroupNodeResult {

    @SerializedName("count_item")
    private int count_item = 0;

    @SerializedName("list")
    private List<SubGroupNodeInfo> list;

    public int getCount_item() {
        return count_item;
    }

    public List<SubGroupNodeInfo> getList() {
        return list;
    }


}
