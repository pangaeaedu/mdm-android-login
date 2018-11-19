package com.nd.android.mdm.biz.env;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.sp.ISharedPreferenceModel;
import com.nd.android.adhoc.basic.sp.SharedPreferenceFactory;
import com.nd.android.adhoc.basic.util.storage.AdhocFileReadUtil;
import com.nd.android.adhoc.basic.util.storage.AdhocFileWriteUtil;
import com.nd.android.adhoc.basic.util.storage.AdhocStorageUtil;
import com.nd.android.mdm.biz.env.constant.MdmEnvConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
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

    private static final String KEY_ENV_CONFIG = "env_config";
    private static final String FORMAT_CONFIG_PATH = "%s/%s/config.json";
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

            if (mCurIndexk < MdmEnvConstant.ENV_INDEX_DEVELOP || mCurIndexk > MdmEnvConstant.ENV_INDEX_WHOLEWORLD) {
                throw new IllegalArgumentException("The ENV_INDEX value of the META_DATA configuration in the manifest file is invalid: " + mCurIndexk);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }catch (NumberFormatException e){
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
        boolean envConfig = mPreferenceModel.getBoolean(KEY_ENV_CONFIG, false);
        final String sdPath = AdhocStorageUtil.getSdCardPath();
        String filePath = String.format(FORMAT_CONFIG_PATH, sdPath, context.getPackageName());
        File confFile = new File(filePath);
        InputStream inputStream = null;
        try {
            boolean fileExists = false;
            AssetManager am = context.getAssets();
            inputStream = am.open(CONFIG_FILE_NAME);
            if (inputStream == null) {
                return;
            }
            if (!confFile.exists() || !envConfig) {
                fileExists = AdhocFileWriteUtil.writeFile(confFile, inputStream) && confFile.exists() && confFile.length() > 0;
            }

            StringBuilder strBuilder;
            if (fileExists) {
                strBuilder = AdhocFileReadUtil.readFile(filePath, "utf-8");
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                strBuilder = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    strBuilder.append(line);
                }
                in.close();
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
                if (!jsonObject.isNull("envs_index")) {
                    mCurIndexk = jsonObject.getInt("envs_index");
                }
            }
        } catch (IOException e) {
            Logger.e(TAG, "EnvUtil read assets file failed:" + e.getMessage());
        } catch (JSONException e) {
            Logger.e(TAG, "EnvUtil read json file exception:" + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Logger.e(TAG, "EnvUtil read close inputstream failed:" + e.getMessage());
            }
        }

        mPreferenceModel.applyPutBoolean(KEY_ENV_CONFIG, true);
        int env = mPreferenceModel.getInt("env", mCurIndexk);
        setCurEnvironment(env);
    }

    public IMdmEnvModule getMdmEnvModel(int index) {
        if (index < mMdmEnvModules.size() && index >= 0) {
            return mMdmEnvModules.get(index);
        } else {
            return new MdmEnvModuleDefault();
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
        mPreferenceModel.putInt("env",mCurIndexk).apply();

        notifyEnvChanged(old, mCurMdmEnvModule);
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

    public void addEnvChangedListener(@NonNull IEnvChangedListener pListener){
        mEnvChangedListeners.add(pListener);
    }

    public void removeEnvChangedListener(@NonNull IEnvChangedListener pListener){
        mEnvChangedListeners.remove(pListener);
    }

    public void notifyEnvChanged(@Nullable IMdmEnvModule pOld, @NonNull IMdmEnvModule pNew){
        for (IEnvChangedListener listener : mEnvChangedListeners) {
            listener.onEnvironmentChanged(pOld, pNew);
        }
    }
}