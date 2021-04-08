package com.nd.android.adhoc.login.basicService.http;

import com.nd.android.adhoc.basic.net.dao.AdhocHttpDao;


// TODO：用 DeviceActivateDaoHelper DeviceUserDao 替换
@Deprecated
public class SetAssetCodeDao extends AdhocHttpDao {

    public SetAssetCodeDao(String pBaseUrl) {
        super(pBaseUrl);
    }
//
//    public EnrollUserInfoResult setAssetCode(String strDeviceToken, String strAssetCode) throws Exception{
//        if(TextUtils.isEmpty(strDeviceToken) || TextUtils.isEmpty(strAssetCode)){
//            return null;
//        }
//        try {
//            Map<String, String> header = null;
//            header = new HashMap<>();
//            header.put("Accept", "application/json");
//
//            String url = "/v1.1/enroll/userInfo";
//
//            EnrollUserInfoResult response = postAction().post(url, EnrollUserInfoResult.class, new EnrollUserInfo(strDeviceToken, 1, strAssetCode),
//                    header);
//            return response;
//        }catch (Exception pE){
//            Logger.e("lsj", "SchoolGroupCodeDao error happpen setAssetCode:"+pE.getMessage());
//            throw pE;
//        }
//    }
}
