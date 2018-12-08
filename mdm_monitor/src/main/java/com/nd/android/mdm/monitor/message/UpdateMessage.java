package com.nd.android.mdm.monitor.message;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 6-14.
 */
public class UpdateMessage extends AbstractMessage {
    public static final Parcelable.Creator<UpdateMessage> CREATOR = new Parcelable.Creator<UpdateMessage>() {

        @Override
        public UpdateMessage createFromParcel(Parcel source) {
            return new UpdateMessage(source.readString(), source.readInt(), source.readInt(), source.readLong());
        }

        @Override
        public UpdateMessage[] newArray(int size) {
            return new UpdateMessage[size];
        }
    };
    private String packageName;
    private int targetVersion;
    private int baseVersion;
    private long sessionid;

    public UpdateMessage(String packageName, int targetVersion, int baseVersion, long sessionid) {
        this.packageName = packageName;
        this.baseVersion = baseVersion;
        this.targetVersion = targetVersion;
        this.sessionid = sessionid;
    }

    @Override
    public String toString() {
        try {
            json.put("cmd", "update");
            JSONObject data = new JSONObject();
            json.put("data", data);
            data.put("packagename", packageName);
            data.put("newversion", targetVersion);
            data.put("oldversion", baseVersion);
            data.put("sessionid", sessionid);
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
        dest.writeString(packageName);
        dest.writeInt(targetVersion);
        dest.writeInt(baseVersion);
        dest.writeLong(sessionid);
    }
}
