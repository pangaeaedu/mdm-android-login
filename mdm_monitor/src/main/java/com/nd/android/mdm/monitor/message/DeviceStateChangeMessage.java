package com.nd.android.mdm.monitor.message;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 8-23.
 */

public class DeviceStateChangeMessage extends AbstractMessage {
    private boolean isCharging;
    private int level;
    private String mac;
    private String linkSpeed;

    public static final Parcelable.Creator<DeviceStateChangeMessage> CREATOR = new Parcelable.Creator<DeviceStateChangeMessage>() {

        @Override
        public DeviceStateChangeMessage createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public DeviceStateChangeMessage[] newArray(int size) {
            return new DeviceStateChangeMessage[0];
        }
    };

    public DeviceStateChangeMessage(boolean isCharging, int level, String mac,String linkSpeed) {
        this.isCharging = isCharging;
        this.level = level;
        this.mac = mac;
        this.linkSpeed = linkSpeed;
    }

    @Override
    public String toString() {
        try {
            JSONObject json = null;
            json = new JSONObject();
            JSONObject data= new JSONObject();
            json.put("data",data);
            data.put("level",level);
            data.put("status",isCharging);
            data.put("mac",mac);
            data.put("linkspeed",linkSpeed);
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
