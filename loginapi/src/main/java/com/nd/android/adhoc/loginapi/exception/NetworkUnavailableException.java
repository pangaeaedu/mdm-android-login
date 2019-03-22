package com.nd.android.adhoc.loginapi.exception;

import com.nd.android.adhoc.loginapi.R;

public class NetworkUnavailableException extends BaseInitException {
    public NetworkUnavailableException(){
        super("NetworkUnavailableException");
    }

    @Override
    public String getMessage() {
        return getContext().getString(R.string.exception_network_unavailable);
    }
}
