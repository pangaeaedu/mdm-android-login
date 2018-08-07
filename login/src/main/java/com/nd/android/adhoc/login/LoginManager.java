package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.communicate.impl.MdmTransferFactory;
import com.nd.android.adhoc.communicate.push.listener.IPushConnectListener;
import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.config.LoginSpConfig;
import com.nd.android.adhoc.login.exception.UnLoginException;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginManager {
    private static final LoginManager ourInstance = new LoginManager();

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private boolean mActivated = false;

    private IPushConnectListener mPushConnectListener = new IPushConnectListener() {
        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected() {

        }
    };

    private Subscription mSubscription = null;
    private LoginManager() {
        startPushSDK();
    }

    private void startPushSDK(){
        MdmTransferFactory.getPushModel().addConnectListener(mPushConnectListener);
        MdmTransferFactory.getPushModel().start();
    }

    public void login(@NonNull String pUserName, @NonNull String pPassword,
                      @NonNull ILoginListener pListener){
        if(!mActivated){
            pListener.onFailed(new UnLoginException());
        }

        if(mSubscription != null){
            mSubscription.unsubscribe();
            mSubscription = null;
        }

        mSubscription= getLoginObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ILoginResult>() {
                    @Override
                    public void onCompleted() {
                        mSubscription = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSubscription = null;

//                        List<ILoginListener> listeners = new ArrayList<>(mListeners);
//                        for (ILoginListener listener : listeners) {
//                            listener.onFailed(e);
//                        }
                    }

                    @Override
                    public void onNext(ILoginResult pResult) {
//                        List<ILoginListener> listeners = new ArrayList<>(mListeners);
//                        for (ILoginListener listener : listeners) {
//                            listener.onSuccess(pResult);
//                        }
                    }
                });
    }

    private Observable<ILoginResult> getLoginObservable(){
       return Observable.create(new Observable.OnSubscribe<ILoginResult>() {
            @Override
            public void call(Subscriber<? super ILoginResult> pSubscriber) {
                try {
                    boolean isActivated = getConfig().isActivated();
                    if(isActivated){
                        ILoginResult result = new LoginResultImpl();
                        pSubscriber.onNext(result);
                        pSubscriber.onCompleted();
                    }
                }catch (Exception e){
                    pSubscriber.onError(e);
                }
            }
        });
    }
    public void logout(){
//        mListeners.clear();
        MdmTransferFactory.getPushModel().stop();
    }

    private LoginSpConfig getConfig(){
        return BasicServiceFactory.getInstance().getConfig();
    }

}
