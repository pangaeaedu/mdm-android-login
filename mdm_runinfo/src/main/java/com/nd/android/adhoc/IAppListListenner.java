package com.nd.android.adhoc;

import android.content.pm.PackageInfo;
import androidx.annotation.Nullable;

/**
 * Created by linsj on 2019/03/26.
 */
public interface IAppListListenner {
    void onRetrievedAppList(@Nullable PackageInfo[] installedList, @Nullable PackageInfo[] runningList);
}
