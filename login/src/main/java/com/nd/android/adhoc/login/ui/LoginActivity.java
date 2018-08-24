package com.nd.android.adhoc.login.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nd.android.adhoc.basic.ui.activity.AdhocBaseActivity;
import com.nd.android.adhoc.basic.ui.util.AdhocActivityUtils;
import com.nd.android.adhoc.login.R;
import com.nd.android.adhoc.loginapi.ILoginResult;
import com.nd.android.adhoc.login.ui.dialog.EnvironmentSettingDialog;
import com.nd.android.adhoc.login.ui.widget.CircleImageView;
import com.nd.android.adhoc.login.ui.widget.SystemPropertiesUtils;
import com.nd.android.adhoc.login.ui.widget.UserInputAnimation;
import com.nd.android.adhoc.login.ui.widget.edit.AdHocEditText;
import com.nd.android.adhoc.login.ui.widget.edit.action.AdHocEditAction;
import com.nd.android.adhoc.login.ui.widget.edit.strategy.style.UnderlineStyle;
import com.nd.android.adhoc.login.ui.widget.spinner.CommonAppCompatSpinner;
import com.nd.android.adhoc.router_api.facade.annotation.Route;

import de.greenrobot.event.EventBus;

/**
 * Created by richsjeson on 2018/1/18.
 * 自组网登录页面
 * //使用loaderManager处理操作逻辑
 */
@Route(path = "/component_main/login_activity")
public class LoginActivity extends AdhocBaseActivity implements View.OnClickListener,
        CommonAppCompatSpinner.OnItemSelectPopListener, ILoginPresenter.IView{

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

        mPresenter = new LoginPresenterImpl(this);
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
    }

    private void addListener() {
        btnSubmitUserLogin.setOnClickListener(this);
        mEnvironmentSetting.setOnClickListener(this);
        EventBus.getDefault().register(this);
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
        //判断设备是否被激活过，如果被激活过，则执行跳转至主页面
        // TODO: yhq 由于引用失败，暂时注释
//        if (ActivateManager.getInstance().isActivatedState()) {
//            Logger.e("HYK","LoginActivity onResume");
//            jumpMain();
//        }
    }


    // TODO: yhq 由于引用失败，暂时注释
//    @Override
//    public Loader onCreateLoader(int id, Bundle args) {
//        if (mUserLoader == null) {
//            mUserLoader = new UserLoader(this, this, this);
//        }
//        return mUserLoader;
//    }
//
//    @Override
//    public void onLoadFinished(Loader loader, Object data) {
//        if (data instanceof Integer) {
//            Integer code = (Integer) data;
//            switch (code) {
//                case -1:
//                    MdmToastModule.getInstance().showWarningToast(getResources().getString(com.nd.pad.nett.R.string.login_input_name));
//                    //用户名为空
//                    break;
//                case -2:
//                    MdmToastModule.getInstance().showWarningToast(getResources().getString(com.nd.pad.nett.R.string.login_input_pwd));
//                    //密码为空
//                    break;
//                case 1:
//                    //校验成功，正在通讯
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader) {
//    }

    // TODO: yhq 由于引用失败，暂时注释
//    @Override
//    public void onSuccess(String userName, String password) {
//        Log.e("HYK", "LoginActivity onSuccess: userName = " + userName + ", password = " + password);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                MdmActivateUtils.doActivate(LoginActivity.this, ActivateManager.getInstance().getUserToken(), LoginActivity.this);
//            }
//        });
//    }

    // TODO: yhq 由于引用失败，暂时注释
//    @Override
//    public void onFailed(final int errCode, final String errMsg) {
//        //切换面板
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isSpringKeyborad = true;
//                if (errCode == ErrorCode.ERR_ACTIVATE_USER_TOKEN_INVALID) {
//                    userLoginError(getResources().getString(R.string.user_input_error));
//                    passwordLoginError(getResources().getString(R.string.passwd_input_error));
//                    MdmToastModule.getInstance().showWarningToast(errMsg);
//                } else if (errCode == ErrorCode.RESULT_ACTIVATE_DEVICE_USED) {
//                    MdmToastModule.getInstance().showWarningToast(getResources().getString(R.string.wifi_device_lock));
//                } else if (errCode == ErrorCode.ERR_ACTIVATE_PROFILE_FAILED) {
//                    //TODO -13的判断
//                    MdmToastModule.getInstance().showWarningToast(getResources().getString(R.string.wifi_device_lock));
//                } else if (errCode == ErrorCode.ERR_ACTIVATE_UC_LOGIN_FAILED) {
//                    MdmToastModule.getInstance().showWarningToast(errMsg);
//                } else if (errCode == ErrorCode.ERR_ACTIVATE_ILLEGAL_ARGUMENT) {
//                    MdmToastModule.getInstance().showWarningToast(getResources().getString(R.string.mdm_login_argument_invalid));
//                } else {
//                    MdmToastModule.getInstance().showWarningToast(getResources().getString(R.string.wifi_device_lock));
//                }
//
//                mLoginPanel.setVisibility(View.VISIBLE);
//                mLoginStatus.setVisibility(View.GONE);
//            }
//        }, 200);
//
//    }
//
//
//    @Override
//    public void onConnect() {
//        Logger.e("HYK","LoginActivity onConnect");
//        jumpMain();
//    }
//
//    @Override
//    public void onDisconnect(Throwable pThrowable) {
//        AdhocException exception = AdhocException.newException(pThrowable);
//        onFailed(exception.getErrorCode(), exception.getMessage());
//    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mInputAnimation.onDestory();
        cilvUserLogin.addAction(null);
        cilvPasswdLogin.addAction(null);
        //销毁Handler
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        mPresenter.onDestroy();
        super.onDestroy();
    }

    /**
     * 跳转至主页
     */
    private void jumpMain() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        finish();
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
    public void onLoginSuccess(ILoginResult pResult) {
        jumpMain();
    }

    @Override
    public void onLoginFailed(Throwable pThrowable) {
        Log.e(TAG, "onLoginFailed:"+pThrowable);
        mLoginPanel.setVisibility(View.VISIBLE);
        mLoginStatus.setVisibility(View.GONE);
    }


//    @Override
//    public void onSettings(int position) {
//        this.envIndex = position;
//    }
}


