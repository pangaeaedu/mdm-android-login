package com.nd.android.adhoc.login.ui.widget.edit.action;

import android.view.View;

import com.nd.android.adhoc.login.ui.widget.edit.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richsjeson on 2018/2/13.
 * @author 全局控制器
 */

public class DefaultActionInvoation implements ActionInvocation {

    private AdHocEditAction action;

    private int foucsInterceptorsIndex=0;

    private int changeInterceptorsIndex=0;

    private int onKeyInterceptorsIndex=0;

    private List<Interceptor> interceptors = new ArrayList<Interceptor>();

    public void setAction(AdHocEditAction action) {
        this.action = action;
    }

    public void addInterceptor(Interceptor interceptors) {
        this.interceptors.add(interceptors);
    }

    @Override
    public void focus(boolean focused) {
        if(action ==null){
            return;
        }
        if(foucsInterceptorsIndex == interceptors.size()){
            action.focus(focused);
        }else{
            Interceptor interceptor =interceptors.get(foucsInterceptorsIndex);
            foucsInterceptorsIndex++;
            interceptor.focus(this,focused);
        }
    }

    @Override
    public void change(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if(action ==null){
            return;
        }
        if(changeInterceptorsIndex == interceptors.size()){
            action.change(text,start,lengthBefore,lengthAfter);
        }else{
            Interceptor interceptor =interceptors.get(changeInterceptorsIndex);
            changeInterceptorsIndex++;
            interceptor.change(this,text,start,lengthBefore,lengthAfter);
        }
    }

    @Override
    public void rightIconClick(View view) {
        if(action ==null){
            return;
        }
    }

    @Override
    public void onKeyBack() {
        if(action ==null){
            return;
        }
        if(onKeyInterceptorsIndex == interceptors.size()){
            action.onKeyBack();
        }else{
            Interceptor interceptor =interceptors.get(onKeyInterceptorsIndex);
            onKeyInterceptorsIndex++;
            interceptor.onKeyBack();
        }
    }

    public void release() {
        action = null;
        if (interceptors != null) {
            interceptors.clear();
        }
    }
}
