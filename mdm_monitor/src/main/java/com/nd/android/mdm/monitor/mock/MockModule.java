package com.nd.android.mdm.monitor.mock;

import android.content.Context;
import android.graphics.Point;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.WindowManager;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.android.adhoc.basic.util.thread.AdhocRxJavaUtil;
import com.nd.android.mdm.monitor.SystemControFactory;
import com.nd.pad.systemapp.ISystemControl;
import com.nd.screen.event.MotionEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/3/13.
 */

public class MockModule {
    private static final String TAG = MockModule.class.getSimpleName();
    private static final int EXECUTE_DELAY = 500;
    private static final int MESSAGE_EXECUTE_MOTION = 1;
    private static final int MESSAGE_INSERT_EVENT = 2;
    private static MockModule instance;
//    private HandlerThread mMockThread;
//    private Handler mMockHandler;
    private ArrayList<MockMessage> mMessageQueue = new ArrayList<MockMessage>();
    private AtomicBoolean mExecuting = new AtomicBoolean(false);
    protected ISystemControl mSystemCtrl;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    //    private long mFirstMotionTime = 0;
    private long mouseDownTime = 0;


    public static MockModule getInstance() {
        if (instance == null) {
            synchronized (MockModule.class) {
                if (instance == null) {
                    instance = new MockModule();
                }
            }
        }
        return instance;
    }

    public boolean init(Context pContext) {
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
        bindAidl();
        mMessageQueue.clear();
//        mMockThread = new HandlerThread("mockthread");
//        mMockHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MESSAGE_INSERT_EVENT:
//                        if (msg.obj != null && msg.obj instanceof MockMessage) {
//                            insertMessage((MockMessage) msg.obj);
//                        }
//                        break;
//                    case MESSAGE_EXECUTE_MOTION:
//                        executeMotion();
//                        break;
//                }
//            }
//        };
//        mMockThread.start();

        WindowManager manager = (WindowManager) pContext.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        manager.getDefaultDisplay().getSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;
        return true;
    }

    public boolean release() {
//        EventBus.getDefault().unregister(this);
        return true;
    }

    private void bindAidl() {
        try {
            ISystemControl systemCtrl =
                    SystemControFactory.getInstance().getSystemControl();
            if (systemCtrl != null && systemCtrl.getVersion() > 0) { // check ctrl version
                mSystemCtrl = systemCtrl;
            }
        } catch (RemoteException e) {
            Logger.e(TAG, "bind aidl error: " + e.toString());
        }
    }

    private void unbindAidl() {
        mSystemCtrl = null;
    }

    protected boolean mouseDown(int x, int y, long downtime, long eventtime) {
        if (mouseDownTime != 0) {
            mouseUp(x, y, mouseDownTime, eventtime);
        }
        mouseDownTime = downtime;
        try {
            mSystemCtrl.sendMotionEvent(downtime, eventtime, android.view.MotionEvent.ACTION_DOWN, x, y, 0);
            Logger.d(TAG, "do mouse down: " + String.format("mouse down:(%d,%d)", x, y));
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected boolean mouseUp(int x, int y, long downtime, long eventtime) {
        try {
            mSystemCtrl.sendMotionEvent(downtime, eventtime, android.view.MotionEvent.ACTION_UP, x, y, 0);
            Logger.d(TAG, "do mouse up: " + String.format("mouse up:(%d,%d)", x, y));
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        mouseDownTime = 0;
        return true;
    }

    protected boolean mouseMove(int x, int y, long downtime, long eventtime) {
        long now = SystemClock.uptimeMillis();
        if (Math.abs(now - eventtime) > EXECUTE_DELAY) {
            return false;
        }
        try {
            mSystemCtrl.sendMotionEvent(downtime, eventtime, android.view.MotionEvent.ACTION_MOVE, x, y, 0);
            Logger.d(TAG, "do mouse move: " + String.format("mouse move:(%d,%d);delay:%d", x, y, Math.abs(now - eventtime)));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void insertMessage(MockMessage msg) {
        mMessageQueue.add(msg);
        if (mExecuting.compareAndSet(false, true)) {
//            mMockHandler.sendEmptyMessage(MESSAGE_EXECUTE_MOTION);
            mockOperate(MESSAGE_EXECUTE_MOTION, null, 0);
        }
    }


    private Subscription mMockOperateSub;

    private void mockOperate(final int pMsgType, final MockMessage pMockMsg, final long pDelay) {
        AdhocRxJavaUtil.doUnsubscribe(mMockOperateSub);
        mMockOperateSub = AdhocRxJavaUtil.safeSubscribe(Observable.timer(pDelay, TimeUnit.MILLISECONDS)
                .map(new Func1<Long, Void>() {
                    @Override
                    public Void call(Long aLong) {
                        if(pMsgType == MESSAGE_EXECUTE_MOTION){
                            executeMotion();
                        }
                        if(pMsgType == MESSAGE_INSERT_EVENT){
                            insertMessage(pMockMsg);
                        }

                        return null;
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mockOperate(pMsgType, pMockMsg, pDelay);
                    }
                }));
    }

    private void executeMotion() {
        if (mSystemCtrl == null) {
            return;
        }
        MockMessage mockMessage = mMessageQueue.remove(0);
        long now = SystemClock.uptimeMillis();
        switch (mockMessage.action) {
            case MotionEvent.LBUTTON_DOWN:
                mouseDown((int) mockMessage.x, (int) mockMessage.y, now, now);
                break;
            case MotionEvent.LBUTTON_UP:
                mouseUp((int) mockMessage.x, (int) mockMessage.y, mouseDownTime, now);
                break;
            case MotionEvent.MOUSE_MOVE:
                mouseMove((int) mockMessage.x, (int) mockMessage.y, mouseDownTime, mockMessage.executeTime);
                break;
        }
        if (mMessageQueue.size() > 0) {
//            mMockHandler.sendEmptyMessage(MESSAGE_EXECUTE_MOTION);
            AdhocRxJavaUtil.doUnsubscribe(mMockOperateSub);
        } else {
            mExecuting.set(false);
        }
    }

//    public void onEvent(MockEvent event) {
//        List<MotionEvent> motionEvents = event.motionEvents;
//        if (motionEvents.size() < 0) {
//            return;
//        }
//        long mFirstMotionTime = motionEvents.get(0).msecs;
//        long now = uptimeMillis();
//        for (MotionEvent motionEvent : event.motionEvents) {
//            if (motionEvent instanceof MouseEvent) {
//                long delay = motionEvent.msecs - mFirstMotionTime;
////                Message msg = mMockHandler.obtainMessage(MESSAGE_INSERT_EVENT);
//                MockMessage mockMessage  = new MockMessage(now + delay, ((MouseEvent) motionEvent).type, ((MouseEvent) motionEvent).x * mScreenWidth, ((MouseEvent) motionEvent).y * mScreenHeight);
////                mMockHandler.sendMessageDelayed(msg, delay);
//                mockOperate(MESSAGE_INSERT_EVENT, mockMessage, delay);
//            }
//            // keyboard event
//        }
//    }

    public void updateConnectStatus(boolean pIsConnected) {
        if (pIsConnected) {
            bindAidl();
        } else {
            unbindAidl();
        }
    }

//    public void onEvent(SystemCtrlConnectEvent event) {
//        if (event.connect) {
//            bindAidl();
//        } else {
//            unbindAidl();
//        }
//    }

    static class MockMessage {
        long executeTime;
        int action;
        float x;
        float y;

        MockMessage(long executeTime, int action, float x, float y) {
            this.executeTime = executeTime;
            this.action = action;
            this.x = x;
            this.y = y;
        }
    }
}
