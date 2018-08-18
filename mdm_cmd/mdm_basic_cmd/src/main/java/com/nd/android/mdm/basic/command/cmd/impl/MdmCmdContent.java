package com.nd.android.mdm.basic.command.cmd.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.nd.android.mdm.basic.command.cmd.ICmdContent_MDM;

import org.json.JSONObject;

/**
 * Created by HuangYK on 2018/5/3.
 */
class MdmCmdContent implements ICmdContent_MDM {

    private String mCmdName;
    private JSONObject mCmdJson;

    private String mSessionId;

    private int mFrom;
    private int mTo;
    private int mCmdType;

    MdmCmdContent(@NonNull String pCmdName, @NonNull JSONObject pCmdJson, @NonNull String pSessionId, int pFrom, int pTo, int pCmdType) {
        mCmdName = pCmdName;
        mCmdJson = pCmdJson;
        mSessionId = pSessionId;
        mFrom = pFrom;
        mTo = pTo;
        mCmdType = pCmdType;
    }

    @Override
    public int getFrom() {
        return mFrom;
    }

    @Override
    public int getTo() {
        return mTo;
    }

    @Override
    public int getCmdType() {
        return mCmdType;
    }


    @NonNull
    @Override
    public String getCmdName() {
        return mCmdName;
    }

    @NonNull
    @Override
    public JSONObject getCmdJson() {
        return mCmdJson;
    }

    @Nullable
    @Override
    public String getSessionId() {
        return mSessionId;
    }


}
