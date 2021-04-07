package com.nd.android.aioe.group.info.dao.api.bean;

import java.util.List;

public class GroupPathsResult {
    private int errcode;
    private List<PathResult> result;

    public List<PathResult> getResult() {
        return result;
    }

    public int getErrcode() {
        return errcode;
    }

    public static class PathResult{
        private String groupcode = "";
        private String name = "";

        public String getName() {
            return name;
        }
    }
}
