package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class QueryActivateUserResultException extends BaseInitException {

    public QueryActivateUserResultException(String pMsg){
        super(pMsg);
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();

        if(msg.equalsIgnoreCase("010")){
            return getContext().getString(R.string.exception_activate_user_010);
        }

        if(msg.equalsIgnoreCase("020")){
            return getContext().getString(R.string.exception_activate_user_020);
        }

        if(msg.equalsIgnoreCase("030")){
            return getContext().getString(R.string.exception_activate_user_030);
        }

        if(msg.equalsIgnoreCase("040")){
            return getContext().getString(R.string.exception_activate_user_040);
        }

        if(msg.equalsIgnoreCase("050")){
            return getContext().getString(R.string.exception_activate_user_050);
        }

        return msg;
    }
}
