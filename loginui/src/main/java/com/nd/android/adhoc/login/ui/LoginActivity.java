package com.nd.android.adhoc.login.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nd.adhoc.assistant.sdk.deviceInfo.DeviceStatus;
import com.nd.android.adhoc.basic.common.toast.AdhocToastModule;
import com.nd.android.adhoc.basic.frame.constant.AdhocRouteConstant;
import com.nd.android.adhoc.basic.frame.factory.AdhocFrameFactory;
import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.ui.activity.AdhocBaseActivity;
import com.nd.android.adhoc.basic.ui.util.AdhocActivityUtils;
import com.nd.android.adhoc.basic.util.app.AdhocAppUtil;
import com.nd.android.adhoc.login.ui.dialog.EnvironmentSettingDialog;
import com.nd.android.adhoc.login.ui.widget.CircleImageView;
import com.nd.android.adhoc.login.ui.widget.SystemPropertiesUtils;
import com.nd.android.adhoc.login.ui.widget.UserInputAnimation;
import com.nd.android.adhoc.login.ui.widget.edit.AdHocEditText;
import com.nd.android.adhoc.login.ui.widget.edit.action.AdHocEditAction;
import com.nd.android.adhoc.login.ui.widget.edit.strategy.style.UnderlineStyle;
import com.nd.android.adhoc.login.ui.widget.spinner.CommonAppCompatSpinner;
import com.nd.android.adhoc.loginapi.IInitApi;
import com.nd.android.adhoc.loginapi.LoginApiRoutePathConstants;
import com.nd.android.adhoc.loginapi.exception.AutoLoginMeetUserLoginException;
import com.nd.android.adhoc.router_api.facade.Postcard;
import com.nd.android.adhoc.router_api.facade.annotation.Route;
import com.nd.android.adhoc.router_api.facade.callback.NavCallback;
import com.nd.android.mdm.biz.env.IEnvChangedListener;
import com.nd.android.mdm.biz.env.IMdmEnvModule;
import com.nd.android.mdm.biz.env.MdmEvnFactory;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by richsjeson on 2018/1/18.
 * 自组网登录页面
 * //使用loaderManager处理操作逻辑
 */
@Route(path = LoginUiRoutePathConstants.PATH_LOGINUI_LOGINUI)
public class LoginActivity extends AdhocBaseActivity implements View.OnClickListener,
        CommonAppCompatSpinner.OnItemSelectPopListener, ILoginPresenter.IView, IEnvChangedListener {

    private static final String TAG = "LoginActivity";

    //用户账号
    private AdHocEditText cilvUserLogin;
    //用户密码
    private AdHocEditText cilvPasswdLogin;
    //账号记录控件
    private CommonAppCompatSpinner mAccountRecordSpinner;
    //账号记录存储器
    private AccountRecordAdapter mAccountRecordAdapter;
    //账号记录控件--移动至某个账号时，放大该账号的布局
    private TextView mMoveShapeTextView;
    //登录按钮
    private Button btnSubmitUserLogin;

    private ProgressDialog progressDialog;
    private Context mContext;
    private Handler mHandler;
    private RelativeLayout mLoginPanel;
    private RelativeLayout mLoginStatus;
    //环境设置按钮
    private TextView mEnvironmentSetting;
    /**
     * 环境选项
     */
    private int envIndex = -1;

    private CircleImageView mProfileImage;

    private View mStatusBar;
    //判断软键盘是否弹出
    private boolean isSpringKeyborad = false;


    private UserInputAnimation mInputAnimation;

    private ILoginPresenter mPresenter = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mHandler = new Handler();
        isLandScape();
        //初始化控件ID
        initView();
        addListener();
        initEnvBtn();

        mPresenter = new LoginPresenterImpl(this);

        MdmEvnFactory.getInstance().addEnvChangedListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        //用户名
        cilvUserLogin = (AdHocEditText) findViewById(R.id.cilv_user_login);
        //密码
        cilvPasswdLogin = (AdHocEditText) findViewById(R.id.cilv_password_login);
        //登录按钮
        btnSubmitUserLogin = (Button) findViewById(R.id.btn_submit_user_login);
        mLoginPanel = (RelativeLayout) findViewById(R.id.ll_login_panel);
        mLoginStatus = (RelativeLayout) findViewById(R.id.ll_login_status);
        mEnvironmentSetting = (TextView) findViewById(R.id.tv_btn_select_environment);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        cilvPasswdLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mStatusBar = findViewById(R.id.status_bar_login);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStatusBar.getLayoutParams();
        params.height = AdhocActivityUtils.getStatusBarHeight(this);
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mStatusBar.setLayoutParams(params);
        mStatusBar.setBackground(getResources().getDrawable(R.drawable.bg_statusbar));
//        setImageResource(getResources().getDrawable(R.drawable.btn_selector_settings_toolbar));

        //只有横屏的情况下，才有这个Logo
        ImageView logo = findViewById(R.id.iv_login_logo);
        if(logo != null){
            logo.setBackgroundResource(R.mipmap.ic_launcher);
        }
    }

    private void initEnvBtn() {
        if (AdhocAppUtil.isDebuggable(this)) {
            mEnvironmentSetting.setVisibility(View.VISIBLE);
            mEnvironmentSetting.setOnClickListener(this);
        } else {
            mEnvironmentSetting.setVisibility(View.GONE);
            mEnvironmentSetting.setOnClickListener(null);
        }
    }

    private void addListener() {
        btnSubmitUserLogin.setOnClickListener(this);
        cilvUserLogin.setEditStyle(new UnderlineStyle());
        cilvPasswdLogin.setEditStyle(new UnderlineStyle());

        mInputAnimation = new UserInputAnimation();

        cilvUserLogin.addAction(new AdHocEditAction() {
            @Override
            public void focus(boolean isFocus) {

                if (isFocus && !isSpringKeyborad) {
                    mInputAnimation.focus(mLoginPanel, mProfileImage);
                    isSpringKeyborad = true;
                }
            }

            @Override
            public void change(CharSequence text, int start, int lengthBefore, int lengthAfter) {

            }

            @Override
            public void rightIconClick(View view) {
                System.out.println("rightIconClick");
            }

            @Override
            public void onKeyBack() {
                mInputAnimation.unfocus(mLoginPanel, mProfileImage);
                isSpringKeyborad = false;
            }
        });

        cilvPasswdLogin.addAction(new AdHocEditAction() {
            @Override
            public void focus(boolean isFocus) {

                if (isFocus && !isSpringKeyborad) {

                    //判断键盘是否弹出，如果键盘没有弹出，则使用
                    mInputAnimation.focus(mLoginPanel, mProfileImage);
                    isSpringKeyborad = true;
                }
            }

            @Override
            public void change(CharSequence text, int start, int lengthBefore, int lengthAfter) {

            }

            @Override
            public void rightIconClick(View view) {

            }

            @Override
            public void onKeyBack() {
                mInputAnimation.unfocus(mLoginPanel, mProfileImage);
                isSpringKeyborad = false;
            }
        });
    }

    //是否强制横竖屏
    private void isLandScape() {
        SystemPropertiesUtils.getInstance().setupProperties(this, new SystemPropertiesUtils.SetupCallBack() {
            @Override
            public void isOrientation(int orientation, boolean isLand) {
                setRequestedOrientation(orientation);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.cilv_password_login) {
            deleteCharts();
        } else if (view.getId() == R.id.cilv_user_login) {
            //弹出账户列表的spinner TODO 目标暂时没有此操作
            popHistoryAccountRecord();
        } else if (view.getId() == R.id.btn_submit_user_login) {
            //执行登录操作
            hideKeyBoard(view);
            mInputAnimation.unfocus(mLoginPanel, mProfileImage);
            login();
        } else if (view.getId() == R.id.tv_btn_select_environment) {
            //弹出对话框选择
            EnvironmentSettingDialog fragment = EnvironmentSettingDialog.newInstance();
//            fragment.setOnEnvironmentSettingsListener(this);
            getSupportFragmentManager().beginTransaction().add(fragment, "").commitAllowingStateLoss();
        }
    }

    //逐个删除字符串
    private void deleteCharts() {
        //如果输入的是密码
        int keyCode = KeyEvent.KEYCODE_DEL;
        KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
        KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
        cilvPasswdLogin.onKeyDown(keyCode, keyEventDown);
        cilvPasswdLogin.onKeyUp(keyCode, keyEventUp);
    }

    //弹出历史记录
    private void popHistoryAccountRecord() {
        if (mAccountRecordAdapter == null) {
            mAccountRecordAdapter = new AccountRecordAdapter(LoginActivity.this);
        }
        if (mAccountRecordSpinner == null) {
            mAccountRecordSpinner = new CommonAppCompatSpinner(LoginActivity.this);
            mAccountRecordSpinner.setAdapter(mAccountRecordAdapter);
            mAccountRecordSpinner.setWidth(cilvUserLogin.getWidth());
            mAccountRecordSpinner.setHeight(300);
//            mAccountRecordSpinner.setAnchorView(cilvUserLogin);
            mAccountRecordSpinner.setOnItemSelectPopListener(this);

            mMoveShapeTextView = new TextView(LoginActivity.this);
            mMoveShapeTextView.setTextSize(38);
            mMoveShapeTextView.setBackgroundColor(Color.WHITE);
            mMoveShapeTextView.setText("44444");
            mMoveShapeTextView.setFocusable(false);
            mMoveShapeTextView.setClickable(false);
            mAccountRecordSpinner.setSelectionView(mMoveShapeTextView);

        }
        if (!mAccountRecordSpinner.isShowing()) {
            mAccountRecordSpinner.show(cilvUserLogin);
            mAccountRecordAdapter.notifyDataSetChanged();
        } else {
            mAccountRecordSpinner.dismiss();
        }
    }

    private void login() {
        if (TextUtils.isEmpty(cilvUserLogin.getText())) {
            userLoginError(getResources().getString( R.string.login_input_name));
            return;
        }
        if (TextUtils.isEmpty(cilvPasswdLogin.getText())) {
            passwordLoginError(getResources().getString( R.string.login_input_pwd));
            return;
        }
        loginUser(cilvUserLogin.getText().toString().trim(), cilvPasswdLogin.getText().toString().trim());
    }

    @Override
    public void onItemSelected(View view, int position) {
        //读取mView的值进行设置
        mMoveShapeTextView.setText(mAccountRecordAdapter.getItem(position));
    }

    @Override
    public void onItemClick(View view, int position) {
        //选中的值。获取出账号记录
        //TODO zyb 样式选中后的数据回调
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = this;
    }

    @Override
    public void onDestroy() {
        mInputAnimation.onDestory();
        cilvUserLogin.addAction(null);
        cilvPasswdLogin.addAction(null);
        //销毁Handler
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        mPresenter.onDestroy();
        MdmEvnFactory.getInstance().removeEnvChangedListener(this);
        super.onDestroy();
    }

    /**
     * 跳转至主页
     */
    private void jumpMain() {
        AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(AdhocRouteConstant.PATH_AFTER_LOGIN)
                .navigation(this, new NavCallback() {

                    @Override
                    public void onInterrupt(@NonNull Postcard postcard) {
                        super.onInterrupt(postcard);
                        Logger.w(TAG, "onInterrupt");
                        protectFinish();
                    }

                    @Override
                    public void onLost(@NonNull Postcard postcard) {
                        super.onLost(postcard);
                        Logger.e(TAG, "onLost");
                        protectFinish();
                    }

                    @Override
                    public void onArrival(@NonNull Postcard postcard) {
                        Logger.w(TAG, "onInterrupt");
                        protectFinish();
                    }
                });
    }

    private void protectFinish(){
        if(!isFinishing()){
            finish();
        }
    }

//    public void onEventMainThread(final LoginMdmDebugEvent event) {
//        loginUser(event.username, event.password);
//    }

    /**
     * 执行用户登录
     *
     * @param userName   用户名
     * @param userPasswd 密码
     */
    void loginUser(final String userName, final String userPasswd) {
        /**
         * 入参时需要校验参数是否为Null或为""
         */
        //切换面板
        mLoginPanel.setVisibility(View.GONE);
        mLoginStatus.setVisibility(View.VISIBLE);

        mPresenter.login(userName, userPasswd);
//        //
//        if (mUserLoader == null) {
//            mUserLoader = new UserLoader(this, this, this);
//        }
//        //由于登入的速度过快，导致loading框不显示的问题
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mUserLoader.requestLoader(userName, userPasswd, envIndex);
//            }
//        }, 200);

    }

    private void hideKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    void userLoginError(String errorName) {
        cilvUserLogin.setHint(errorName);
        cilvUserLogin.setHintTextColor(getResources().getColor(R.color.error_exception));
        cilvUserLogin.setRightDrawable(getResources().getDrawable(R.drawable.icon_error_input_exception));
        cilvUserLogin.setFocusable(true);
        cilvUserLogin.setFocusableInTouchMode(true);
        cilvUserLogin.requestFocus();
        cilvUserLogin.setWaringRender();
    }

    void passwordLoginError(String errorName) {
        cilvPasswdLogin.setHint(errorName);
        cilvPasswdLogin.setHintTextColor(getResources().getColor(R.color.error_exception));
        cilvPasswdLogin.setRightDrawable(getResources().getDrawable(R.drawable.icon_error_input_exception));
        cilvPasswdLogin.setFocusable(true);
        cilvPasswdLogin.setFocusableInTouchMode(true);
        cilvPasswdLogin.requestFocus();
        cilvPasswdLogin.setWaringRender();
    }


    /**
     * 设置厂家的ICON
     *
     * @param drawable
     */
    void setImageResource(Drawable drawable) {
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 1.3), (int) (drawable.getIntrinsicHeight() * 1.3));
        mEnvironmentSetting.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mInputAnimation.onDestory();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void cancelLoading() {

    }

    @Override
    public void onLoginSuccess(DeviceStatus pStatus) {
        if(DeviceStatus.isStatusUnLogin(pStatus)){
            AdhocToastModule.getInstance().showToast(getString(R.string.login_error_user_not_activated));
            mLoginPanel.setVisibility(View.VISIBLE);
            mLoginStatus.setVisibility(View.GONE);
            return;
        }

        jumpMain();
    }

    @Override
    public void onLoginFailed(Throwable pThrowable) {
        if(pThrowable instanceof AutoLoginMeetUserLoginException){
            showErrorToast(pThrowable);
            jumpMain();
            return;
        }

        Logger.e(TAG, "onLoginFailed:" + pThrowable);
        showErrorToast(pThrowable);
        mLoginPanel.setVisibility(View.VISIBLE);
        mLoginStatus.setVisibility(View.GONE);
    }

    private void showErrorToast(Throwable pThrowable) {
        Toast.makeText(this, pThrowable.getMessage(), Toast.LENGTH_LONG).show();;
    }

    @Override
    public void onEnvironmentChanged(@Nullable IMdmEnvModule pOld, @NonNull IMdmEnvModule pNew) {
        showLoading();

        IInitApi api = (IInitApi) AdhocFrameFactory.getInstance().getAdhocRouter()
                .build(LoginApiRoutePathConstants.PATH_LOGINAPI_INIT).navigation();
        if (api == null) {
            AdhocToastModule.getInstance().showToast(getString(R.string.init_api_not_found));
            return;
        }

        Logger.i(TAG, "begin to query device status");
        api.queryDeviceStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DeviceStatus>() {
                    @Override
                    public void onCompleted() {
                        cancelLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        cancelLoading();
                    }

                    @Override
                    public void onNext(DeviceStatus pStatus) {
                        if(DeviceStatus.isStatusUnLogin(pStatus)){
                            return;
                        }

                        jumpMain();
                    }
                });
    }
}


