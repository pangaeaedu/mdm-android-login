package com.nd.android.adhoc.login.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nd.android.adhoc.login.ui.widget.spinner.CommonAppCompatSpinner;
import com.nd.android.adhoc.login.ui.widget.spinner.PopSpinnerAdapter;

/**
 * Created by richsjeson on 2018/1/19.
 * 账号历史列表
 * 来源：点击登录后，会自动将账号存至SP。
 */

public class AccountRecordAdapter extends PopSpinnerAdapter<String> {

    public AccountRecordAdapter(Context context) {
        super(context);
        mList.add("12313213123");
        mList.add("12313213123");
        mList.add("12313213123");
    }


    @Override
    public CommonAppCompatSpinner.ViewHolder bindHoler() {
        return new BinderViewHolder();
    }


    static class BinderViewHolder extends CommonAppCompatSpinner.ViewHolder<String>{
        private TextView mResultView;

        @Override
        public View onCreateViewHolder(@NonNull Context pContext) {
            return LayoutInflater.from(pContext).inflate(R.layout.list_item_main,null);
//            mResultView= (TextView) parent.findViewById(R.id.tv_item_lsv_main);
        }

        @Override
        public void onBinderView(@NonNull Context pContext, String pItemData) {

//            if(mList != null && mList.size()>0){
//                mResultView.setText((String)mList.get(position));
//            }
        }
    }



}
