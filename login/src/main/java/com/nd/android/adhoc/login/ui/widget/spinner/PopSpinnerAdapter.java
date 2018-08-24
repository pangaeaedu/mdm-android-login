package com.nd.android.adhoc.login.ui.widget.spinner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;

import com.nd.android.adhoc.basic.common.util.AdhocDataCheckUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HuangYK on 2018/5/23.
 */

public abstract class PopSpinnerAdapter<T> extends BaseAdapter implements SpinnerAdapter {

    protected List<T> mList;

    private Context mContext;

    public PopSpinnerAdapter(@NonNull Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    public T getItem(int position) {
        if (position < 0 || position > getCount()) {
            return null;
        }
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonAppCompatSpinner.ViewHolder<T> bindHolder = null;
        if (convertView == null) {
            bindHolder = bindHoler();
            convertView = bindHolder.onCreateViewHolder(mContext);
            convertView.setTag(bindHolder);
            bindHolder.onBinderView(mContext, getItem(position));
        } else {
            bindHolder = (CommonAppCompatSpinner.ViewHolder<T>) convertView.getTag();
            bindHolder.onBinderView(mContext, getItem(position));
        }
        return convertView;
    }

    public void setData(List<T> list){
        mList.clear();
        if (!AdhocDataCheckUtils.isCollectionEmpty(list)) {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public abstract CommonAppCompatSpinner.ViewHolder<T> bindHoler();

    public List<T> getData(){
        return mList;
    }

    public void release() {
        mContext = null;
        if (mList != null) {
            mList.clear();
            mList = null;
        }
    }
}
