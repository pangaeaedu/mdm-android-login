package com.nd.android.adhoc.login.ui.widget.edit.interceptor;

import android.view.View;

import com.nd.android.adhoc.login.ui.widget.edit.action.ActionInvocation;


/**
 * Created by richsjeson on 2018/2/13.
 * @author zyb 拦截器接口
 */

public interface Interceptor {

    void before(ActionInvocation invocation, String method);

    void after(ActionInvocation invocation, String method);
    /**
     * 焦点聚焦
     */
    void focus(ActionInvocation invocation, boolean isFocus);

    /**
     * 文本输入时触发
     */
    void change(ActionInvocation invocation, CharSequence text, int start, int lengthBefore, int lengthAfter);

    /**
     * 传入参数值
     * @param invocation
     */
    void rightIconClick(ActionInvocation invocation, View view);

    void onKeyBack();
}
