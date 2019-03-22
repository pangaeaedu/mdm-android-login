package com.nd.android.adhoc.login.processOptimization.login;

import com.nd.android.adhoc.loginapi.exception.BaseInitException;

public class LoginUserOrPwdEmptyException extends BaseInitException {
    public LoginUserOrPwdEmptyException(){
        super("login user or password can not be empty");
    }
}
