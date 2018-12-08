package com.nd.android.mdm.monitor.message;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 6-14.
 */
public class BatteryChangeMessage extends AbstractMessage {
    public static final Parcelable.Creator<BatteryChangeMessage> CREATOR = new Parcelable.Creator<BatteryChangeMessage>() {

        @Override
        public BatteryChangeMessage createFromParcel(Parcel source) {
            return new BatteryChangeMessage(source.readInt(), source.readByte() == 1, source.readString());
        }

        @Override
        public BatteryChangeMessage[] newArray(int size) {
            return new BatteryChangeMessage[size];
        }
    };
    private int level;
    private boolean isCharging;
    private String mac;

    public BatteryChangeMessage(int level, boolean isCharging, String mac) {
        this.level = level;
        this.isCharging = isCharging;
        this.mac = mac;
    }

    @Override
    public String toString() {
        try {
            json.put("cmd", "batterychange");
            JSONObject data = new JSONObject();
            json.put("data", data);
            data.put("level", level);
            data.put("status", isCharging);
            data.put("mac", mac);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeByte(isCharging ? (byte) 1 : 0);
        dest.writeString(mac);
    }
}
