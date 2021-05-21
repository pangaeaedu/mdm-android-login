package com.nd.android.aioe.device.status.biz.api.provider;

import androidx.annotation.WorkerThread;

import com.nd.android.adhoc.basic.common.exception.AdhocException;
import com.nd.android.adhoc.router_api.facade.template.IProvider;
import com.nd.android.aioe.device.status.biz.api.model.GetDeviceStatusModel;

public interface IDeviceStatusProvider extends IProvider {

    String ROUTE_PATH = "/cmp_device_status_biz/status_provider";

    @WorkerThread
    GetDeviceStatusModel getDeviceStatusFromServer() throws AdhocException;

    @WorkerThread
    void updateDeviceStatus() throws AdhocException;

    @WorkerThread
    String getDeviceId() throws AdhocException;

}
