package com.nd.adhoc.assistant.sdk.deviceInfo;

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


    private int mValue = 0;

    DeviceStatus(int pValue){
        mValue = pValue;
    }

    public int getValue(){
        return mValue;
    }

    public static DeviceStatus fromValue(int pValue){
        for (DeviceStatus status : DeviceStatus.values()) {
            if(status.getValue() == pValue){
                return status;
            }
        }

        return Init;
    }

    @Override
    public String toString() {
        return super.toString();

    }

    public static boolean isStatusUnLogin(DeviceStatus pStatus){
        if(pStatus == Init || pStatus == Unknown || pStatus == Enrolled){
            return true;
        }

        return false;
    }
}
