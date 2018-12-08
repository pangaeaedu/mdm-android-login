package com.nd.android.mdm.monitor.message;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 6-14.
 */
public class AccessibilityMessage extends AbstractMessage {
    private boolean accessible;
    private String mac;

    public static final Parcelable.Creator<AccessibilityMessage> CREATOR = new Parcelable.Creator<AccessibilityMessage>() {

        @Override
        public AccessibilityMessage createFromParcel(Parcel source) {
            return new AccessibilityMessage(source.readString(), source.readByte() == 1);
        }

        @Override
        public AccessibilityMessage[] newArray(int size) {
            return new AccessibilityMessage[size];
        }
    };

    public AccessibilityMessage(String mac, boolean accessible) {
        this.accessible = accessible;
        this.mac = mac;
    }

    @Override
    public String toString() {
        try {
            JSONObject data = new JSONObject();
            json.put("data", data);
            json.put("cmd", "accessibility");
            data.put("accessibility", accessible);
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
        dest.writeString(this.mac);
        dest.writeByte(this.accessible ? (byte) 1 : (byte) 0);
    }
}
