package com.nd.android.mdm.monitor.message;

import android.os.Parcelable;

import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;

import org.json.JSONObject;

/**
 * Created by yaoyue1019 on 6-14.
 */
public abstract class AbstractMessage implements Parcelable {

    public abstract String toString();

    protected JSONObject json = new JSONObject();

    public void send() {
//        new MessageEvent(toString()).post();
        MdmTransferFactory.getCommunicationModule().sendMessage(toString());
    }
}
