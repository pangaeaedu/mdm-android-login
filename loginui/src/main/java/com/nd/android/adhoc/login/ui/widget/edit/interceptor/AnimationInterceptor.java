package com.nd.android.adhoc.login.ui.widget.edit.interceptor;

import android.view.View;

import com.nd.android.adhoc.login.ui.widget.edit.action.ActionInvocation;


/**
 * Created by richsjeson on 2018/2/13.
 * @author zyb EditText拦截器
 */

public abstract class AnimationInterceptor implements Interceptor {


    @Override
    public abstract void before(ActionInvocation invocation, String method);


    @Override
    public abstract void after(ActionInvocation invocation, String method);

    @Override
    public void focus(ActionInvocation invocation,boolean isFocus) {
        before(invocation,"focus");
        invocation.focus(isFocus);
        after(invocation,"focus");
    }

    @Override
    public void change(ActionInvocation invocation, CharSequence text, int start, int lengthBefore, int lengthAfter) {
        before(invocation,"change");
        invocation.change(text,start,lengthBefore,lengthAfter);
        after(invocation,"change");
    }

    @Override
    public void rightIconClick(ActionInvocation invocation,View view) {
        before(invocation,"rightIconClick");
        invocation.rightIconClick(view);
        after(invocation,"rightIconClick");
    }


}
