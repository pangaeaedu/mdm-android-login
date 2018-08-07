package com.nd.android.adhoc.login;

import android.support.annotation.NonNull;

import com.nd.android.adhoc.login.basicService.BasicServiceFactory;
import com.nd.android.adhoc.login.basicService.config.LoginSpConfig;
import com.nd.android.adhoc.login.exception.UnLoginException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginManager {
    private static final LoginManager ourInstance = new LoginManager();

    private List<ILoginListener> mListeners = new CopyOnWriteArrayList<>();

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private boolean mActivated = false;

    private Subscription mSubscription = null;
    private LoginManager() {
        startPushSDK();
    }

    public void addListener(@NonNull ILoginListener pListener){
        mListeners.add(pListener);
    }

    public void removeListener(@NonNull ILoginListener pListener){
        mListeners.remove(pListener);
    }

    private void startPushSDK(){

    }

    private void onPushSDKResult(){

    }

    public void login(String pUserName, String pPassword) throws Exception{
        if(!mActivated){
            throw new UnLoginException();
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

                        List<ILoginListener> listeners = new ArrayList<>(mListeners);
                        for (ILoginListener listener : listeners) {
                            listener.onFailed(e);
                        }
                    }

                    @Override
                    public void onNext(ILoginResult pResult) {
                        List<ILoginListener> listeners = new ArrayList<>(mListeners);
                        for (ILoginListener listener : listeners) {
                            listener.onSuccess(pResult);
                        }
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
        mListeners.clear();
    }

    private LoginSpConfig getConfig(){
        return BasicServiceFactory.getInstance().getConfig();
    }

}
