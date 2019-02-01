package com.nd.adhoc.assistant.sdk.deviceInfo;

//"status":1  //1=入库，2=在用，3=锁定，4=丢失，5=故障，6=淘汰
public enum DeviceStatus {
    Unknown(0),
    Enrolled(1),
    Activated(2),
    Locked(3),
    Lost(4),
    Malfunction(5),
    WeedOut(6);


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

        return Unknown;
    }

    @Override
    public String toString() {
        return super.toString();

    }
}
