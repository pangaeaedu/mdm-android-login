package com.nd.android.mdm.runinfo.business.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuangYK on 2018/12/7.
 */

public class AppRunPackageInfoBean {

    private List<Integer> mHourList;
    private List<Long> mDurationList;
    private List<Integer> mCountList;
    private int mSize = 0;
    private String mPackageName;


    public AppRunPackageInfoBean(String packageName) {
        mPackageName = packageName;

        mHourList = new ArrayList<>();
        mDurationList = new ArrayList<>();
        mCountList = new ArrayList<>();
    }

    public void addPackageInfo(int hour, long time, int count){
        mHourList.add(hour);
        mDurationList.add(time);
        mCountList.add(count);
        mSize ++;
    }

    public List<Integer> getHourList() {
        return mHourList;
    }

    public List<Long> getDurationList() {
        return mDurationList;
    }

    public List<Integer> getCountList() {
        return mCountList;
    }

    public int getSize() {
        return mSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppRunPackageInfoBean that = (AppRunPackageInfoBean) o;

        return mPackageName.equals(that.mPackageName);
    }

    @Override
    public int hashCode() {
        return mPackageName.hashCode();
    }
}
