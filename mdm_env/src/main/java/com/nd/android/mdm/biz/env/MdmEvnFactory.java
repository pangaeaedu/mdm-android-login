package com.nd.android.mdm.biz.env;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.mdm.biz.env.constant.MdmEnvConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by HuangYK on 2018/2/28.
 */
public final class MdmEvnFactory {

    private static final String TAG = "MdmEvnFactory";

    private static final String CONFIG_FILE_NAME = "envconf.json";
    private volatile static MdmEvnFactory sInstance = null;
    private final List<IMdmEnvModule> mMdmEnvModules = new CopyOnWriteArrayList<>();
    private IMdmEnvModule mCurMdmEnvModule;
    private int mCurIndexk = MdmEnvConstant.DEFAULT_ENV_INDEX;

    private ISharedPreferenceModel mPreferenceModel = null;

    private List<IEnvChangedListener> mEnvChangedListeners = new CopyOnWriteArrayList<>();

    private MdmEvnFactory() {
        Context context = AdhocBasicConfig.getInstance().getAppContext();
        loadManifestConfig(context);
        init(context);
    }

    private void loadManifestConfig(Context pContext) {
        try {
            ApplicationInfo appInfo = pContext.getPackageManager()
                    .getApplicationInfo(pContext.getPackageName(),
                            PackageManager.GET_META_DATA);

            if (!appInfo.metaData.containsKey("ENV_INDEX")) {
                throw new IllegalArgumentException("The ENV_INDEX value of the META_DATA configuration in the manifest file does not exist.");
            }

            mCurIndexk = appInfo.metaData.getInt("ENV_INDEX");

//            if (mCurIndexk < MdmEnvConstant.ENV_INDEX_DEVELOP || mCurIndexk > MdmEnvConstant.ENV_INDEX_MAX) {
//                throw new IllegalArgumentException("The ENV_INDEX value of the META_DATA configuration in the manifest file is invalid: " + mCurIndexk);
//            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }

    public static MdmEvnFactory getInstance() {
        if (sInstance == null) {
            synchronized (MdmEvnFactory.class) {
                if (sInstance == null) {
                    sInstance = new MdmEvnFactory();
                }
            }
        }
        return sInstance;
    }

    private void init(@NonNull Context context) {
        mPreferenceModel = SharedPreferenceFactory.getInstance().getModel(context, context.getPackageName());
        InputStream inputStream = null;
        BufferedReader br = null;
        try {
            AssetManager am = context.getAssets();
            inputStream = am.open(CONFIG_FILE_NAME);
            if (inputStream == null) {
                return;
            }

            StringBuilder strBuilder;
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            strBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                strBuilder.append(line);
            }

            if (!TextUtils.isEmpty(strBuilder)) {
                JSONObject jsonObject = new JSONObject(strBuilder.toString());
                mMdmEnvModules.clear();
                JSONArray jsonEnvs = jsonObject.getJSONArray("envs");
                for (int i = 0; i < jsonEnvs.length(); i++) {
                    Gson gson = new GsonBuilder().create();
                    IMdmEnvModule mdmEnvModule = gson.fromJson(String.valueOf(jsonEnvs.get(i)), MdmEnvModule.class);
                    mMdmEnvModules.add(mdmEnvModule);
                }
            }
        } catch (Exception e) {
            Logger.e(TAG, "init, read assets file error:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logger.e(TAG, "init, close inputstream error:" + e.getMessage());
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Logger.e(TAG, "init, close bufferedReader error:" + e.getMessage());
                }
            }
        }

        int env = mPreferenceModel.getInt("env", mCurIndexk);
        setCurEnvironment(env);
    }

    @Nullable
    public IMdmEnvModule getMdmEnvModel(int index) {
        if (index < mMdmEnvModules.size() && index >= 0) {
            return mMdmEnvModules.get(index);
        } else {
            return null;
        }
    }

    public void setCurEnvironment(int index) {
        if (index < 0 || index >= mMdmEnvModules.size()) {
            return;
        }

        if (mCurIndexk == index && mCurMdmEnvModule != null) {
            return;
        }

        IMdmEnvModule old = getMdmEnvModel(mCurIndexk);
        mCurIndexk = index;
        mCurMdmEnvModule = getMdmEnvModel(index);
        mPreferenceModel.putInt("env", mCurIndexk).apply();

        EnvUtils.setUcEnv(mCurIndexk);

        notifyEnvChanged(old, mCurMdmEnvModule);
        broadcastNewEnv(index);
    }

    private void broadcastNewEnv(int pIndex) {
//        Context context = AdhocBasicConfig.getInstance().getAppContext();
//        Intent intent = new Intent(context, MdmEnvBroadcastReceiver.class);
//        intent.setAction(MdmEnvBroadcastReceiver.ACTION_NAME);
//        intent.putExtra(MdmEnvBroadcastReceiver.ENV_VALUE, pIndex);
//
//        context.sendBroadcast(intent);
    }


    @NonNull
    public IMdmEnvModule getCurEnvironment() {
        // 保证返回的环境数据不会为空
        if (mCurMdmEnvModule == null) {
            setCurEnvironment(mCurIndexk);
        }
        return mCurMdmEnvModule;
    }

    public int getCurIndex() {
        return mCurIndexk;
    }

    public void addEnvChangedListener(@NonNull IEnvChangedListener pListener) {
        mEnvChangedListeners.add(pListener);
    }

    public void removeEnvChangedListener(@NonNull IEnvChangedListener pListener) {
        mEnvChangedListeners.remove(pListener);
    }

    public void notifyEnvChanged(@Nullable IMdmEnvModule pOld, @NonNull IMdmEnvModule pNew) {
        for (IEnvChangedListener listener : mEnvChangedListeners) {
            listener.onEnvironmentChanged(pOld, pNew);
        }
    }
}