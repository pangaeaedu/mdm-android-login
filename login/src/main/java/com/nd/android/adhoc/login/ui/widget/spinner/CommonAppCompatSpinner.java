package com.nd.android.adhoc.login.ui.widget.spinner;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SpinnerAdapter;

/**
 * Created by richsjeson on 2018/1/19.
 * 创建一个公共的spinner，带滑动效果的spinner
 */

public class CommonAppCompatSpinner extends AbsSpinner implements View.OnTouchListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    /**
     * 容器类,将外部的包裹起来
     */
    private LinearLayout mContaint;

    private PopupWindow mDropdownPopup;

    private PopSpinnerAdapter mSpinnerAdapater;

    /**
     * 内部列表
     */
    private ListViewCompat mListView;

    private Context mContext;

    private int mDropDownGravity;

//    private View mDropDownAnchorView;

    private View mSelectionView;

    private OnItemSelectPopListener mOnItemClickListener;

    private boolean isShow;

    public CommonAppCompatSpinner(Context context) {
        super(context);
        onCreateView(context);
    }

    public CommonAppCompatSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreateView(context);
    }

    public CommonAppCompatSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreateView(context);
    }

    public void onCreateView(Context context) {
        mContaint = new LinearLayout(context);
        mContaint.setOrientation(LinearLayout.VERTICAL);
        this.mContext = context;
        mDropdownPopup = new PopupWindow(this.mContext);
        mDropdownPopup.setWidth(LayoutParams.WRAP_CONTENT);
        mDropdownPopup.setHeight(LayoutParams.WRAP_CONTENT);
        mDropdownPopup.setContentView(mContaint);
        //添加列表项
        mListView = new ListViewCompat(this.mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mListView.setLayoutParams(layoutParams);
        mListView.setDividerHeight(0);
        mListView.setBackgroundColor(Color.BLUE);
        mListView.setOnTouchListener(this);
        mContaint.setLayoutParams(layoutParams);
        mContaint.addView(mListView);
    }

    public void setAdapter(SpinnerAdapter adapter) {

        this.mSpinnerAdapater = (PopSpinnerAdapter) adapter;
        mListView.setAdapter(mSpinnerAdapater);
    }


    public void setSelection(int position, boolean animate) {
        if (mListView != null) {
            mListView.setSelection(position);
        }
    }

    public void setSelection(int position) {
        if (mListView != null) {
            mListView.setSelection(position);
        }
    }

    public View getSelectedView() {
        if (mListView != null) {
            return mListView.getSelectedView();
        } else {
            return null;
        }
    }

    public SpinnerAdapter getAdapter() {

        return this.mSpinnerAdapater;
    }

    public int getCount() {
        return this.mSpinnerAdapater.getCount();
    }

    public int pointToPosition(int x, int y) {
        if (mListView != null) {
            return mListView.pointToPosition(x, y);
        } else {
            return -1;
        }
    }

    public void setWidth(int width) {
        this.mContaint.setMinimumWidth(width);
    }

    public void setHeight(int height) {
        this.mContaint.setMinimumHeight(height);
    }

    public void setDropDownGravity(int gravity) {
        mDropDownGravity = gravity;
    }


    public void setSelectionView(@Nullable View selectionView) {

        this.mSelectionView = selectionView;
        this.mContaint.addView(mSelectionView);
        mSelectionView.setVisibility(View.GONE);
    }

    /**
     * 展示
     */
    public void show(@Nullable View pAdhocView) {

        if (mDropdownPopup != null && pAdhocView != null) {
            mDropdownPopup.showAsDropDown(pAdhocView);
            mDropdownPopup.setWidth(pAdhocView.getWidth());
            mDropdownPopup.setHeight(LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(pAdhocView.getWidth(), 300);
            mListView.setLayoutParams(layoutParams);
            mListView.setSelection(0);
            isShow = true;
        }
    }

    /**
     * 关闭
     */
    public void dismiss() {
        if (mDropdownPopup != null) {
            mDropdownPopup.dismiss();
//            mDropdownPopup=null;
            mSelectionView.setVisibility(View.GONE);
            isShow = false;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        long position = mListView.pointToRowId((int) event.getX(), (int) event.getY());

        if (position < 0) {

        } else {
            View view = mListView.getChildAt((int) position);
            if (view != null) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_MOVE:
                        if (mSelectionView != null) {
                            mSelectionView.setBackgroundColor(Color.CYAN);
                            mSelectionView.setX(view.getX());
                            mSelectionView.setY(view.getY());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(view.getWidth(), view.getHeight() * 2);
                            mSelectionView.setLayoutParams(params);
                            mSelectionView.setVisibility(View.VISIBLE);
                            mOnItemClickListener.onItemSelected(view, (int) position);
                        }

                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mOnItemClickListener.onItemClick(view, (int) position);
                        break;

                }
            }

        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        this.onItemClick(adapterView, view, position, l);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//        Toast.makeText(mContext,"position:"+ mListView.getSelectedItemPosition(),Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnItemSelectPopListener {

        /**
         * 选中事件
         *
         * @param view
         * @param position
         */
        void onItemSelected(View view, int position);

        /**
         * 点击事件
         *
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);
    }

    public void setOnItemSelectPopListener(OnItemSelectPopListener listener) {
        this.mOnItemClickListener = listener;
    }


    public boolean isShowing() {
        return isShow;
    }


    //缓存类
    public static abstract class ViewHolder<T> {
        /**
         * 创建view
         *
         */
        public abstract View onCreateViewHolder(@NonNull Context pContext);

        /**
         * 绑定数据源
         *
         */
        public abstract void onBinderView(@NonNull Context pContext, T pItemData);
    }

}
