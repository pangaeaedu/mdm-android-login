package com.nd.android.aioe.device.status.biz.api.constant;

//  "status":1  //0=未知，1=入库，3=丢失,4=在用，5=故障，6=锁定，7=淘汰
public enum DeviceStatus {
    Init(-1),
    Unknown(0),
    Enrolled(1),
    Lost(3),
    Activated(4),
    Malfunction(5),
    Locked(6),
    WeedOut(7);


    private int mValue;
    private boolean mIsDeleted = false;

    DeviceStatus(int pValue) {
        mValue = pValue;
    }

    public int getValue() {
        return mValue;
    }

    public void setIsDeleted(boolean isDeleted) {
        mIsDeleted = isDeleted;
    }

    public boolean isDeleted() {
        return mIsDeleted;
    }

    public boolean isUnActivated() {
        return this == Init || this == Unknown || this == Enrolled || isDeleted();
    }

    public static DeviceStatus fromValue(int pValue) {
        for (DeviceStatus status : DeviceStatus.values()) {
            if (status.getValue() == pValue) {
                return status;
            }
        }

        return Init;
    }

}
