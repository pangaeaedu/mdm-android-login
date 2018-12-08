package com.nd.android.mdm.monitor.message;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 6-14.
 */
@Deprecated
public class VRWearMessage extends AbstractMessage {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<VRWearMessage>(){

        @Override
        public VRWearMessage createFromParcel(Parcel source) {
            return new VRWearMessage(source.readString());
        }

        @Override
        public VRWearMessage[] newArray(int size) {
            return new VRWearMessage[size];
        }
    };

    private String mac;

    public VRWearMessage(String mac) {
        this.mac = mac;
    }

    public VRWearMessage() {

    }

    @Override
    public String toString() {
        try {
            JSONObject data = new JSONObject();
            json.put("cmd", "vrwear");
            json.put("data", data);
//            data.put("wear", SystemProps.getHmtMounted());
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
        dest.writeString(mac);
    }
}
