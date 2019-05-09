package com.nd.android.adhoc.utils;


import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2019/03/25.
 */

public class AppRunInfoReportUtils {
    /**
     * 获取当前的时数
     * @return
     */
    public static int getCurrentHour(){
        return getHourOfSpecifyTime(System.currentTimeMillis());
    }

    public static int getHourOfSpecifyTime(long lTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(lTime));
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前的分钟数
     * @return
     */
    public static int getCurrentMinute(){
        return getMinuteOfSpecifyTime(System.currentTimeMillis());
    }

    public static int getMinuteOfSpecifyTime(long lTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(lTime));
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取小时下的毫秒数
     * @return
     */
    public static long getCurrentMSInHour(){
        return getMSInHourOfSpecifyTime(System.currentTimeMillis());
    }

    public static long getMSInHourOfSpecifyTime(long lTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(lTime));
        return (calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND)) * 1000 + calendar.get(Calendar.MILLISECOND);
    }

    /**
     * 获取当前小时的时间戳
     * @return
     */
    public static long getCurrentHourTimeStamp(){
        long lCurTimeStamp = System.currentTimeMillis();
        return getSpecifyTimeHourStamp(lCurTimeStamp);
    }

    /**
     * 获取指定小时的小时时间戳
     * @return
     */
    public static long getSpecifyTimeHourStamp(long lTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(lTime));
        int iMinute = calendar.get(Calendar.MINUTE);
        int iSecond = calendar.get(Calendar.SECOND);
        int iMs = calendar.get(Calendar.MILLISECOND);

        return lTime - iMinute * 60 * 1000L - iSecond * 1000L - iMs;
    }
}
