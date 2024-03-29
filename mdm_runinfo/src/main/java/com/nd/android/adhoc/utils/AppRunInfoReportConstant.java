package com.nd.android.adhoc.utils;


/**
 * Created by Administrator on 2019/03/25.
 */

public class AppRunInfoReportConstant {
    /**缓存正在运行的APP列表，每10分钟写一次缓存，DB版*/
    public static final String OPS_SP_KEY_CACHE_RUNNING_APP_MAP_VDB = "OPS_SP_KEY_CACHE_RUNNING_APP_MAP_VDB";
    /**写本地缓存时间点的KEY*/
    public static final String OPS_SP_KEY_CACHE_TIME = "OPS_SP_KEY_CACHE_TIME";


    /**缓存正在运行的APP列表*/
    @Deprecated
    public static final String OPS_SP_KEY_CACHE_RUNNING_APP_MAP = "OPS_SP_KEY_CACHE_RUNNING_APP_MAP";
    /**缓存当前时段的APP运行情况，旧版，不再使用*/
    @Deprecated
    public static final String OPS_SP_KEY_CACHE_APP_LIST = "OPS_SP_KEY_CACHE_APP_LIST";
    /**上报失败，存到这里*/
    @Deprecated
    public static final String OPS_SP_KEY_CACHE_FAILED_REPORTED_APP_LIST = "OPS_SP_KEY_CACHE_FAILED_REPORTED_APP_LIST";
    /**缓存需要上报的数据的KEY*/
    @Deprecated
    public static final String OPS_SP_KEY_CACHE_TO_REPORT_DATA = "OPS_SP_KEY_CACHE_TO_REPORT_DATA";
    /**缓存上次上报时间点（无论成功还是失败）的KEY*/
    @Deprecated
    public static final String OPS_SP_KEY_CACHE_LAST_REPORT_TIME = "OPS_SP_KEY_CACHE_LAST_REPORT_TIME";
}
