package com.nd.android.mdm.biz.env;

/**
 * Created by Administrator on 2018/8/14 0014.
 */

public interface IEnvChangedListener {
    void onEnvironmentChanged(IMdmEnvModule pOld, IMdmEnvModule pNew);
}
