package com.nd.android.mdm.monitor.message;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 6-14.
 */
public class UsbAttachMessage extends AbstractMessage {
    public static final Parcelable.Creator<UsbAttachMessage> CREATOR = new Parcelable.Creator<UsbAttachMessage>(){

        @Override
        public UsbAttachMessage createFromParcel(Parcel source) {
            return new UsbAttachMessage(source.readString(),source.readByte() == 1);
        }

        @Override
        public UsbAttachMessage[] newArray(int size) {
            return new UsbAttachMessage[size];
        }
    };
    private String mac;
    private boolean attached;

    public UsbAttachMessage(String mac, boolean attached) {
        this.mac = mac;
        this.attached = attached;
    }

    public UsbAttachMessage(boolean attached) {
        this.attached = attached;
    }

    @Override
    public String toString() {
        try {
            json.put("cmd", "usb");
            JSONObject data = new JSONObject();
            data.put("attached", attached);
            data.put("mac", mac);
            json.put("data", data);
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
        dest.writeString(mac);
        dest.writeByte(attached ? (byte) 1 : 0);
    }
}
