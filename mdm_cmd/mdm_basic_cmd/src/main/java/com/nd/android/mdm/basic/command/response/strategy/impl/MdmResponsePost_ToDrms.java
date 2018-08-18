package com.nd.android.mdm.basic.command.response.strategy.impl;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.constant.AdhocCmdFromTo;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.mdm.basic.command.response.IResponse_MDM;
import com.nd.android.mdm.basic.command.response.strategy.IMdmResponsePost;

/**
 * Created by HuangYK on 2018/5/1.
 */

public class MdmResponsePost_ToDrms implements IMdmResponsePost {

    @NonNull
    @Override
    public AdhocCmdFromTo getPostTo() {
        return AdhocCmdFromTo.MDM_CMD_DRM;
    }

    @Override
    public void post(@NonNull IResponse_MDM pResponse) {
        MdmTransferFactory.getCommunicationModule().doHttpPost(pResponse.getPostUrl(), pResponse.toString());
    }
}
