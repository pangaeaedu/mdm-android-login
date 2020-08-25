package com.nd.android.adhoc.login.basicService.data.http;

import java.util.List;

/**
 * @author Administrator
 * @name adhoc-101-assistant-app
 * @class name：com.nd.android.adhoc.main.ui.bean
 * @class describe
 * @time 2020/8/24 17:04
 * @change
 * @chang time
 * @class describe
 */
public class DeviceGroupPath {
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
