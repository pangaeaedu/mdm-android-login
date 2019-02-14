package com.nd.android.adhoc.login.ui.widget.edit.action;

import android.view.View;

/**
 * Created by richsjeson on 2018/2/13.
 * @author zyb action接口
 */

public interface AdHocEditAction {
    /**
     * 焦点聚焦
     * @param isFocus
     */
    void focus(boolean isFocus);

    /**
     * 文本输入时触发
     * @param text
     * @param start
     * @param lengthBefore
     * @param lengthAfter
     */
    void change(CharSequence text, int start, int lengthBefore, int lengthAfter);

    /**
     *右边的事件
     */
    void rightIconClick(View view);

    void onKeyBack();
}
