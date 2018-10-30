package com.nd.android.adhoc.login.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.nd.adhoc.assistant.sdk.AssistantBasicServiceFactory;
import com.nd.adhoc.assistant.sdk.config.AssistantSpConfig;
import com.nd.android.adhoc.basic.common.toast.AdhocToastModule;
import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.login.R;
import com.nd.android.adhoc.login.ui.widget.DensityUtils;
import com.nd.android.adhoc.login.utils.EnvUtils;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import static com.nd.android.adhoc.login.ui.widget.SystemPropertiesUtils.PROPERTIES_COMMON_DIALOG_HEIGHT_SCALE;
import static com.nd.android.adhoc.login.ui.widget.SystemPropertiesUtils.PROPERTIES_COMMON_DIALOG_WIDTH_SCALE;


public class EnvironmentSettingDialog extends DialogFragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    /**
     * 关闭按钮
     */
    private ImageView ivCloseWindow;
    /**
     * 当前环境
     */
    private TextView mCurrentEnvironment;
    /**
     * 选择环境
     */
    private Spinner mSelectEnvironment;
    /**
     * 连接操作
     */
    private Button btnSubmit;
    /**
     * 环境配置的参数
     */
    private int position=-1;


//    private OnEnvironmentSettingsListener mOnEnvironmentSettingsListener;


    public static EnvironmentSettingDialog newInstance() {
        return new EnvironmentSettingDialog();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_environment_settings, container, false);
        //做适配
        //读取window大小
        int widthPixels = (int) DensityUtils.calDensityWidth(getActivity(), PROPERTIES_COMMON_DIALOG_WIDTH_SCALE);
        int heightPixels = (int) DensityUtils.calDensityHeight(getActivity(), PROPERTIES_COMMON_DIALOG_HEIGHT_SCALE);
        getDialog().getWindow().setLayout(widthPixels,heightPixels);
        initView(view);
        addListener();
        return view;
    }

    private void initView(View view) {
        ivCloseWindow= (ImageView) view.findViewById(R.id.iv_environment_close_window);
        mCurrentEnvironment= (TextView) view.findViewById(R.id.tv_current_environment);
        mSelectEnvironment= (Spinner) view.findViewById(R.id.sp_select_environment);
        btnSubmit= (Button) view.findViewById(R.id.tv_commit_settings_environment);
//        ISharedPreferenceModel preferenceModel = SharedPreferenceFactory.getInstance().getModel(getActivity());
//        int envIndex = preferenceModel.getInt("env", MdmEvnFactory.getInstance().getCurIndex());
//        this.position = envIndex;
        this.position = MdmEvnFactory.getInstance().getCurIndex();
        String[] environment = getResources().getStringArray(R.array.environment);
        mCurrentEnvironment.setText(environment[this.position]);
        //防止初始化时未弹出dialog前默认走onItemSelected方法
        mSelectEnvironment.setSelection(this.position);
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams lp =  getDialog().getWindow().getAttributes();
        //读取window大小
        int widthPixels = (int) DensityUtils.calDensityWidth(getActivity(), PROPERTIES_COMMON_DIALOG_WIDTH_SCALE);
        int heightPixels = (int) DensityUtils.calDensityHeight(getActivity(), PROPERTIES_COMMON_DIALOG_HEIGHT_SCALE);
        lp.width=widthPixels;
        lp.height=heightPixels;
        getDialog().getWindow().setAttributes(lp);
        getDialog().setCancelable(false);
    }

    private void addListener() {
        ivCloseWindow.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        mSelectEnvironment.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_environment_close_window) {
            dismiss();
        } else if(v.getId()==R.id.tv_commit_settings_environment){
            //发送event 事件，保存环境
            this.position=this.mSelectEnvironment.getSelectedItemPosition();
            AdhocToastModule.getInstance().showToast(getString(R.string.current_environment) +
                    mCurrentEnvironment.getText());
            dismiss();

//            ISharedPreferenceModel spModel = SharedPreferenceFactory.getInstance().getModel(getActivity());
//            spModel.putInt("env",this.position).apply();
//            this.mOnEnvironmentSettingsListener.onSettings(this.position);
            getConfig().clearData();
            EnvUtils.setUcEnv(position);
            MdmEvnFactory.getInstance().setCurEnvironment(position);
            MdmTransferFactory.getPushModel().start();
        }
    }

    private AssistantSpConfig getConfig(){
        return AssistantBasicServiceFactory.getInstance().getSpConfig();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //默认初始化对话框时，会自动走该方法，造成环境名称错误。
        String[] environment = getResources().getStringArray(R.array.environment);
        mCurrentEnvironment.setText(environment[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    public interface  OnEnvironmentSettingsListener{
//
//         void onSettings(int position);
//    }


//    public void setOnEnvironmentSettingsListener(OnEnvironmentSettingsListener listener){
//        this.mOnEnvironmentSettingsListener=listener;
//    }
}
