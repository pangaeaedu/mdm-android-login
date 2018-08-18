package com.nd.android.mdm.basic.command.operator;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.basic.command.cmd.ICmdCreator;
import com.nd.android.adhoc.basic.command.cmd.ICmdExecutor;
import com.nd.android.mdm.basic.command.cmd.ICmdContent_MDM;
import com.nd.android.mdm.basic.command.cmd.ICmd_MDM;

/**
 * Created by HuangYK on 2018/8/15.
 */
public abstract class CmdOperatorBase implements ICmdOperator {

    private ICmdCreator<ICmd_MDM, ICmdContent_MDM> mCmdCreator;
    private ICmdExecutor<ICmd_MDM> mCmdExecutor;


    public CmdOperatorBase(@NonNull ICmdCreator<ICmd_MDM, ICmdContent_MDM> pCmdCreator,
                           @NonNull ICmdExecutor<ICmd_MDM> pCmdExecutor) {
        mCmdCreator = pCmdCreator;
        mCmdExecutor = pCmdExecutor;
    }

    @Override
    public boolean operate(@NonNull final ICmdContent_MDM pCmdContent) throws AdhocException {
        if (!isCmdExit(pCmdContent.getCmdName())) {
            return false;
        }

        ICmd_MDM cmd = mCmdCreator.createCmd(pCmdContent);
        if (cmd != null) {
            mCmdExecutor.executeCmd(cmd);
        }
        return true;
    }

    protected abstract boolean isCmdExit(@NonNull String pCmdName);

}
