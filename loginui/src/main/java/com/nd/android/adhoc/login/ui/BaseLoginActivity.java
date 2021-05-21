package com.nd.android.adhoc.login.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.nd.android.adhoc.basic.ui.activity.AdhocBaseActivity;

public abstract class BaseLoginActivity extends AdhocBaseActivity implements ILoginPresenter.IView{

    private ILoginPresenter mPresenter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract ILoginPresenter getPresenter();


}
