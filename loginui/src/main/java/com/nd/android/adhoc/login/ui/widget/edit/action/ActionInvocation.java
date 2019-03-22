package com.nd.android.adhoc.login.ui.widget.edit.action;

import android.view.View;

/**
 * Created by richsjeson on 2018/2/13.
 * @author zyb 调度器
 */

public interface ActionInvocation {

    /**
     * 焦点聚焦
     */
    void focus(boolean isFocus);

    /**
     * 文本输入时触发
     */
    void change(CharSequence text, int start, int lengthBefore, int lengthAfter);

    /**
     *右边的事件
     */
    void rightIconClick(View view);

    /**
     * 键盘关闭事件
     */
    void onKeyBack();
}
