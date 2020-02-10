package com.nd.android.adhoc.db.entity.intfc;


import java.io.Serializable;

/**
 * Created by linsj on 2019/8/23.
 */
public interface IMdmRunInfoEntity extends Serializable {

    /**
     * 获取ID
     * @return
     */
    String getId();

    /**
     * 设置ID
     * @return
     */
    void setId(String strId);

    /**
     * 获取该记录当天凌晨0点时间戳
     *
     * @return templateId
     */
    long getDayBeginTimeStamp();

    /**
     * 设置该0点时间戳
     *
     * @param lTimeStamp 当天0点所在时间戳
     */
    void setDayBeginTimeStamp(long lTimeStamp);

    /**
     * 获取该记录包名
     *
     * @return 包名
     */
    String getPackageName();

    /**
     * 设置该记录包名
     *
     * @param strPackageName 包名
     */
    void setPackageName(String strPackageName);

    /**
     * 获取文件名
     *
     * @return 文件名
     */
    String getAppName();

    /**
     * 设置文件名
     *
     * @param strAppName 文件名
     */
    void setAppName(String strAppName);

    /**
     * 获取运行总时长
     *
     * @return 运行总时长
     */
    long getRunTime();

    /**
     * 调取运行总时长
     *
     * @param lTime 当天运行总时长
     */
    void setRunTime(long lTime);

    /**
     * 获取运行次数
     *
     * @return 运行次数
     */
    int getRunCount();

    /**
     * 设置运行次数
     *
     * @param iRunCount 运行次数
     */
    void setRunCount(int iRunCount);
}
