package com.nd.android.mdm.biz.env;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/8/14 0014.
 */

public interface IEnvChangedListener {
    void onEnvironmentChanged(@Nullable IMdmEnvModule pOld, @NonNull IMdmEnvModule pNew);
}
