package com.nd.android.mdm.basic.command.operator;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.mdm.basic.command.cmd.ICmdContent_MDM;

/**
 * Created by HuangYK on 2018/8/15.
 */

public interface ICmdOperator {

    boolean operate(@NonNull ICmdContent_MDM pCmdContent) throws AdhocException;
}
