package com.nd.adhoc.assistant.sdk.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;


public class BaseSpConfig {
    private String mSpName = "";
    private SharedPreferences mPreferences = null;
    private Context mContext = null;

    public BaseSpConfig(@NonNull Context pContext, @NonNull String pSpName){
        mContext = pContext;
        mSpName = pSpName;
    }

    public void saveString(String key,String value){
        if (TextUtils.isEmpty(key)){
            return;
        }
        getDefault().edit().putString(key,value).apply();
    }

    public void saveLong(String key,long value){
        if (TextUtils.isEmpty(key)){
            return;
        }
        getDefault().edit().putLong(key,value).apply();
    }

    public void saveInt(String key,int value){
        if (TextUtils.isEmpty(key)){
            return;
        }
        getDefault().edit().putInt(key,value).apply();
    }

    public Boolean getBoolean(@NonNull String pKey){
        if(TextUtils.isEmpty(pKey)){
            return false;
        }

        return getDefault().getBoolean(pKey, false);
    }

    public void saveBoolean(String pKey, boolean pValue){
        if (TextUtils.isEmpty(pKey)){
            return;
        }
        getDefault().edit().putBoolean(pKey,pValue).apply();
    }

    public String getString(String key){
        if (TextUtils.isEmpty(key)){
            return "";
        }
        return getDefault().getString(key,"");
    }

    public long getLong(String key){
        if (TextUtils.isEmpty(key)){
            return 0;
        }
        return getDefault().getLong(key,0);
    }

    public long getLong(String key,long defaultValue){
        if (TextUtils.isEmpty(key)){
            return defaultValue;
        }
        return getDefault().getLong(key,defaultValue);
    }

    public int getInt(String key,int defaultValue){
        if (TextUtils.isEmpty(key)){
            return defaultValue;
        }
        return getDefault().getInt(key,defaultValue);
    }

    private SharedPreferences getDefault(){
        if(mPreferences == null){
            if(mContext == null){
                throw new RuntimeException("get content from appfactory failed");
            }

            mPreferences = mContext.getSharedPreferences(mSpName, Context.MODE_PRIVATE);
        }
        return mPreferences;
    }
}
