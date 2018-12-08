package com.nd.android.mdm.monitor.message;

import android.os.Parcel;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 6-14.
 */
public class ModelMessage extends AbstractMessage {
    private String mac;

    public ModelMessage(String mac) {
        this.mac = mac;
    }

    public static final Creator<ModelMessage> CREATOR = new Creator<ModelMessage>() {

        @Override
        public ModelMessage createFromParcel(Parcel source) {
            return new ModelMessage(source.readString());
        }

        @Override
        public ModelMessage[] newArray(int size) {
            return new ModelMessage[size];
        }
    };


    @Override
    public String toString() {
        try {
            json.put("cmd", "model");
            JSONObject data = new JSONObject();
            json.put("data", data);
            data.put("model", new String(Base64.encode(android.os.Build.MODEL.getBytes(), Base64.DEFAULT)));
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
