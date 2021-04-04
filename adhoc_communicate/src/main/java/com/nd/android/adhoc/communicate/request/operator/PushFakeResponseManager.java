package com.nd.android.adhoc.communicate.request.operator;

import com.nd.adhoc.push.core.IPushChannel;
import com.nd.adhoc.push.core.IPushChannelDataListener;
import com.nd.adhoc.push.core.IPushRecvData;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.utils.PushDataUtils;
import com.nd.android.aioe.device.info.config.DeviceInfoSpConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PushFakeResponseManager {
    private static final String TAG = "PushFake";
    private static final PushFakeResponseManager ourInstance = new PushFakeResponseManager();

    public static PushFakeResponseManager getInstance() {
        return ourInstance;
    }

    private Map<String, String> mCmdResultCache = new ConcurrentHashMap<>();

    private PushFakeResponseManager() {
        MdmTransferFactory.getPushModel().addDataListener(new IPushChannelDataListener() {
            @Override
            public void onPushDataArrived(IPushChannel pChannel, IPushRecvData pData) {

            }

            @Override
            public void onMessageSendResult(String pMsgID, int pErrorCode) {
                notifyMessageSendResult(pMsgID, pErrorCode);
            }
        });
    }

    public void addRequest(String pMsgID, String pContent){
        Logger.i(TAG, "addRequest, msgID:" + pMsgID);
        Logger.d(TAG, "addRequest, msgID:" + pMsgID + " addRequest:" + pContent);
        String action = PushDataUtils.getAction(pContent);
        if(action.equalsIgnoreCase("/v1/device/cmdresult/")
                || action.equalsIgnoreCase("/v2/cmd/batchresult/")){
            Logger.i(TAG, "put data, msgID:" + pMsgID);
            Logger.d(TAG, "put data, msgID:" + pMsgID + ", content:" + pContent);
            mCmdResultCache.put(pMsgID, pContent);
        }
    }

    public void notifyMessageSendResult(String pMsgID, int pErrorCode){
        Logger.i(TAG, "notifyMessageSendResult msgID:" + pMsgID + " error code:" + pErrorCode);
        if(mCmdResultCache.containsKey(pMsgID)){
            String fakeMsg = "";
            if(pErrorCode == 0){
                fakeMsg = PushDataUtils.generateResponse(200, pMsgID, 1, 0,
                        DeviceInfoSpConfig.getDeviceID());
            } else {
                fakeMsg = PushDataUtils.generateResponse(505, pMsgID, 1, pErrorCode,
                        DeviceInfoSpConfig.getDeviceID());
            }

            Logger.d(TAG, "fake msg:"+fakeMsg);
            AdhocPushRequestOperator.receiveFeedback(fakeMsg);
            mCmdResultCache.remove(pMsgID);

        }
    }


}
