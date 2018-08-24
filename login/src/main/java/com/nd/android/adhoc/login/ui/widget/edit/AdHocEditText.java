package com.nd.android.adhoc.login.ui.widget.edit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.nd.android.adhoc.login.R;
import com.nd.android.adhoc.login.ui.widget.edit.action.AdHocEditAction;
import com.nd.android.adhoc.login.ui.widget.edit.action.DefaultActionInvoation;
import com.nd.android.adhoc.login.ui.widget.edit.interceptor.Interceptor;
import com.nd.android.adhoc.login.ui.widget.edit.strategy.style.BaseStyle;


/**
 * @author 自组网 公共的登录时的输入控件。
 *         做法:利用AppCompatEditText的特性setCompoundDrawablesWithIntrinsicBounds进行实现
 *         Created by richsjeson on 2018/1/18.
 */

@SuppressLint("AppCompatCustomView")
public class AdHocEditText extends EditText {

    /**
     * 外部传入的right drawable 原始图
     */
    private Drawable mRightDrawable;
    /**
     * 外部传入的 left drawable 原始图
     */
    private Drawable mLeftDrawable;
    /**
     * 外部传入的背景图 原始图
     */
    private Drawable mBackgroundDrawable;
    /**
     * 当EditText对焦时图标所需要呈现的样式颜色值
     */
    private int mRenderIconColor;
    /**
     * 当EditText对焦时背景框所需要呈现的样式颜色值
     */
    private int mRenderFocusBackgroundColor;

    /**
     * 当EditText对焦时背景框所需要呈现的样式颜色值
     */
    private int mRenderWarningBackgroundColor;
    /**
     * 获取ICON设置比例大小
     */
    private float mGlobalScale=1f;
    /**
     * 调度器
     */
    private DefaultActionInvoation mDefaultActionInvoation=null;
    /**
     * 样式工具类
     */
    private AdHocEditStyleHelper mEditStyleHelper =null;

    private TypedArray mTypedArray;


    public AdHocEditText(Context context) {
        super(context);
    }

    public AdHocEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateView(context, attrs);
    }

    public AdHocEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreateView(context, attrs);
    }

    /**
     * 初始化view
     */
    void onCreateView(Context context, AttributeSet attrs) {
        setMaxLines(1);
        setSingleLine();
        initProperties(attrs,context);
        initController();
        //初始化完成后，生成策略类
        mEditStyleHelper =new AdHocEditStyleHelper();
        mDefaultActionInvoation=new DefaultActionInvoation();
    }
    //初始化控件
    private void initController()
    {
        setRightDrawable(mRightDrawable);
        setLeftDrawable(mLeftDrawable);
        setCompoundDrawablePadding(14);
    }
    //初始化Attrs表
    public void initProperties(AttributeSet attrs, Context context) {

        this.mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.AdHoc_Common_InputMethod_EditStyle);
        mLeftDrawable = mTypedArray.getDrawable(R.styleable.AdHoc_Common_InputMethod_EditStyle_leftResId);
        mRightDrawable = mTypedArray.getDrawable(R.styleable.AdHoc_Common_InputMethod_EditStyle_rightResId);
        mBackgroundDrawable = mTypedArray.getDrawable(R.styleable.AdHoc_Common_InputMethod_EditStyle_backgroundResId);
        mRenderIconColor=mTypedArray.getColor(R.styleable.AdHoc_Common_InputMethod_EditStyle_renderIconColor,-1);
        mRenderFocusBackgroundColor=mTypedArray.getColor(R.styleable.AdHoc_Common_InputMethod_EditStyle_renderFocusBackgroundColor,-1);
        mRenderWarningBackgroundColor=mTypedArray.getColor(R.styleable.AdHoc_Common_InputMethod_EditStyle_renderWarningBackgroundColor,-1);
        mGlobalScale=mTypedArray.getFloat(R.styleable.AdHoc_Common_InputMethod_EditStyle_globalScale,1);
    }

    public void addAction(AdHocEditAction action){
        mDefaultActionInvoation.setAction(action);
    }

    /**
     *
     * @param style 对外开放的样式器
     */
    public void setEditStyle(BaseStyle style){
        if (mEditStyleHelper == null) {
            return;
        }
        mEditStyleHelper.setStyle(style);
        mEditStyleHelper.initStyleProperties(this, mGlobalScale, this.mTypedArray);
    }
    /**
     * 加入样式焦点拦截器
     * @param interceptor
     */
    public void setInterceptor(Interceptor interceptor){
        if(mDefaultActionInvoation!= null){
            mDefaultActionInvoation.addInterceptor(interceptor);
        }

    }

    /**
     * 外部暴露出设置左边图标的drawable
     * @param leftDrawable
     */
    public void setLeftDrawable(Drawable leftDrawable) {
        if (leftDrawable == null) {
            if (mRightDrawable == null) {
                setCompoundDrawables(null, null, null, null);
            } else {
                setCompoundDrawables(null, null, mRightDrawable, null);
            }
        } else {
            leftDrawable.setBounds(0, 0, (int) (leftDrawable.getIntrinsicWidth() * mGlobalScale), (int) (leftDrawable.getIntrinsicHeight() * mGlobalScale));
            setCompoundDrawables(leftDrawable, null, mRightDrawable, null);
        }
    }
    /**
     * 外部暴露出设置右边图标的drawable
     * @param rightDrawable
     */
    public void setRightDrawable(Drawable rightDrawable) {
        if (rightDrawable == null) {
            if (mLeftDrawable == null) {
                setCompoundDrawables(null, null, null, null);
            } else {
                setCompoundDrawables(mLeftDrawable, null, null, null);
            }
        } else {
            rightDrawable.setBounds(0, 0, (int) (rightDrawable.getIntrinsicWidth() * mGlobalScale), (int) (rightDrawable.getIntrinsicHeight() * mGlobalScale));
            setCompoundDrawables(mLeftDrawable, null, rightDrawable, null);
        }
    }

    /**
     * 外部暴露出设置左边图标的drawable
     * @param leftDrawableResId 资源Id
     */
    public void setLeftDrawable(@DrawableRes int leftDrawableResId) {
        if (mLeftDrawable == null) {
            if (mRightDrawable == null) {
                setCompoundDrawables(null, null, null, null);
            } else {
                setCompoundDrawables(null, null, mRightDrawable, null);
            }
        } else {
            mLeftDrawable = getResources().getDrawable(leftDrawableResId);
            mLeftDrawable.setBounds(0, 0, (int) (mLeftDrawable.getIntrinsicWidth() * mGlobalScale), (int) (mLeftDrawable.getIntrinsicHeight() * mGlobalScale));
            setCompoundDrawables(mLeftDrawable, null, mRightDrawable, null);
        }
    }
    /**
     * 外部暴露出设置右边图标的drawable
     * @param rightDrawableResId
     */
    public void setRightDrawable(@DrawableRes int rightDrawableResId) {
        if (mRightDrawable == null) {
            if (mLeftDrawable == null) {
                setCompoundDrawables(null, null, null, null);
            } else {
                setCompoundDrawables(mLeftDrawable, null, null, null);
            }
        } else {
            mRightDrawable = getResources().getDrawable(rightDrawableResId);
            mRightDrawable.setBounds(0, 0, (int) (mRightDrawable.getIntrinsicWidth() * mGlobalScale), (int) (mRightDrawable.getIntrinsicHeight() * mGlobalScale));
            setCompoundDrawables(mLeftDrawable, null, mRightDrawable, null);
        }
    }


    /**
     * 外部暴露出设置背景框
     * @param backgroundResId 设置EditText背景框的操作
     */
    public void setBackgroundDrawables(@DrawableRes int backgroundResId) {
        this.mBackgroundDrawable = getResources().getDrawable(backgroundResId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(this.mBackgroundDrawable);
        }else{
            setBackgroundResource(backgroundResId);
        }
    }


    /**
     * 外部暴露设置原始框的drawable
     * @param backgroundDrawable
     */
    public void setBackgroundDrawables(Drawable backgroundDrawable){
        this.mBackgroundDrawable=backgroundDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(backgroundDrawable);
        }else{
            setBackgroundDrawable(backgroundDrawable);
        }
    }

    /**
     * 设置警告的背景色
     */
    @SuppressLint("ResourceType")
    public void setWaringRender() {
        if (mEditStyleHelper == null) {
            return;
        }
        if(mRenderWarningBackgroundColor >-1){
            mEditStyleHelper.onErrorBackground(mRenderIconColor,
                    R.styleable.AdHoc_Common_InputMethod_EditStyle_leftResId,
                    R.styleable.AdHoc_Common_InputMethod_EditStyle_rightResId,
                    R.styleable.AdHoc_Common_InputMethod_EditStyle_backgroundResId);
        }
    }

    /**
     * 复写EditText本身的方法：onTextChanged（）
     * 调用时刻：当输入框内容变化时
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (mDefaultActionInvoation != null) {
            mDefaultActionInvoation.change(text, start, lengthBefore, lengthAfter);
        }
    }

    /**
     * 复写EditText本身的方法：onFocusChanged（）
     * 调用时刻：焦点发生变化时
     */
    @SuppressLint({"NewApi", "ResourceType"})
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (mDefaultActionInvoation != null) {
            mDefaultActionInvoation.focus(focused);
        }
        if (mEditStyleHelper == null) {
            return;
        }
        if (focused) {
            if(mRenderFocusBackgroundColor !=-1) {
               mEditStyleHelper.onFoucsBackground(mRenderFocusBackgroundColor, R.styleable.AdHoc_Common_InputMethod_EditStyle_leftResId,R.styleable.AdHoc_Common_InputMethod_EditStyle_rightResId,R.styleable.AdHoc_Common_InputMethod_EditStyle_backgroundResId);
           }
        } else {
            if(mRenderIconColor!=-1){
                mEditStyleHelper.onUnFocusBackground(mRenderIconColor,R.styleable.AdHoc_Common_InputMethod_EditStyle_leftResId,R.styleable.AdHoc_Common_InputMethod_EditStyle_rightResId,R.styleable.AdHoc_Common_InputMethod_EditStyle_backgroundResId);
            }
        }
    }


     @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //如果右边为空
                if (mRightDrawable == null) {
                    //打开焦点
                    setFocusable(true);
                    setFocusableInTouchMode(true);
                    requestFocus();
                    return super.onTouchEvent(event);
                }
                //如果右边有ICON，则
                Drawable drawable = mRightDrawable;
                if (drawable != null && event.getX() <= (getWidth() - getPaddingRight())
                        && event.getX() >= (getWidth() - getPaddingRight() - drawable.getBounds().width())) {
                    setFocusable(false);
                    setFocusableInTouchMode(false);
                    if (mDefaultActionInvoation != null) {
                        mDefaultActionInvoation.rightIconClick(this);
                    }
                }
                break;
            case  MotionEvent.ACTION_DOWN:
                //默认没有焦点，如果需要焦点点击打开焦点
                if (mRightDrawable == null) {
                    //恢复焦点
                    setFocusable(true);
                    setFocusableInTouchMode(true);
                    requestFocus();
                    onFocusChanged(true, View.FOCUS_DOWN,new Rect());
                    return super.onTouchEvent(event);
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mEditStyleHelper == null) {
            return;
        }
        mEditStyleHelper.onDraw(canvas);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mDefaultActionInvoation != null) {
                mDefaultActionInvoation.onKeyBack();
            }
        }
        return false;
    }

    public void onDestroy(){
        if (mDefaultActionInvoation != null) {
            mDefaultActionInvoation.release();
            mDefaultActionInvoation = null;
        }
        if (mEditStyleHelper != null) {
            mEditStyleHelper.release();
            mEditStyleHelper = null;
        }
    }
}
