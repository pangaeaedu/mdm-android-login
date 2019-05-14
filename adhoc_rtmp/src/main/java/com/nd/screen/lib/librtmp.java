package com.nd.screen.lib;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.nd.screen.activity.DataArriveCallback;
import com.nd.screen.activity.ScreencastActivity;
import com.nd.screen.event.StartMonitorEvent;
import com.nd.screen.interfaces.ScreenCaptureCallback;
import com.nd.screen.interfaces.ScreenCaptureDataCallback;
import com.nd.screen.ui.FloatCamera;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/3/29.
 */
public class librtmp implements ScreenCaptureDataCallback {
    public static final int TYPE_NONE = -1;
    public static final int TYPE_CAMERA_BACK = 0;
    public static final int TYPE_CAMERA_FRONT = 1;
    public static final int TYPE_SCREEN = 2;
    private static final String TAG = librtmp.class.getSimpleName();
    private static librtmp sInstance = new librtmp();
    private static List<ScreenCaptureCallback> sScreenCaptureCallbacks = new CopyOnWriteArrayList<>();
    private static int sysVersion = Integer.parseInt(Build.VERSION.SDK);

    static {
        try {
            System.loadLibrary("rtmp_module");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    private AtomicBoolean mStarted = new AtomicBoolean(false);
    private AtomicBoolean mRtmpConnected = new AtomicBoolean(false);

    //    private static FileOutputStream fos;
    private String mUrl;
    private int mWidth;
    private int mHeight;
    private int mFrameRate;
    private int mBitRate;
    private int mType = TYPE_NONE;
    private int mQuality;
    private Context mContext;
    private AtomicBoolean mConnecting = new AtomicBoolean(false);
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private long mLastRestartTimestampMs = 0;
    private DataArriveCallback sSendCallback = new DataArriveCallback() {
        @Override
        public void onDataArrive(byte[] cmd, int cmdLen) {
            if (cmd != null && cmdLen > 0) {
//                Log4jLogger.d("rtmp send bytes:" + cmdLen);
                librtmp.getInstance().sendVideoStream(cmd, cmdLen);
            }
        }
    };

    public static native boolean native_connect(String url, int width, int height, int frameRate);

    public static native void native_close();

    public static native boolean native_sendH264_packet(byte[] data, int len);

    public static librtmp getInstance() {
        return sInstance;
    }

    public synchronized void addScreenCaptureCallback(ScreenCaptureCallback callback) {
        if (callback != null && !sScreenCaptureCallbacks.contains(callback)) {
            sScreenCaptureCallbacks.add(callback);
        }
    }

    public synchronized void removeScreenCaptureCallback(ScreenCaptureCallback callback) {
        if (callback != null && sScreenCaptureCallbacks.contains(callback)) {
            sScreenCaptureCallbacks.remove(callback);
        }
    }

    public boolean start(final String url, final int width, final int height, final int frameRate, final int bitRate, final Context context) {
        if (mConnecting.get()) {
            return false;
        }
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mUrl = url;
                mWidth = width;
                mHeight = height;
                mFrameRate = frameRate;
                mBitRate = bitRate;
                mContext = context;
                mStarted.set(true);
                startInputStream(TYPE_SCREEN);
                doConnect();
            }
        });
        return true;
    }

    public boolean startCameraCapture(final String url, final int width, final int height, final int frameRate, final int bitRate, final int type, final int quality, final Context context) {
        if (mConnecting.get()) {
            return false;
        }
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                mUrl = url;
                mWidth = width;
                mHeight = height;
                mFrameRate = frameRate;
                mBitRate = bitRate;
                mContext = context;
                mQuality = quality;
                mStarted.set(true);
                startInputStream(type);
                doConnect();
            }
        });
        return true;
    }

    private boolean doConnect() {
        boolean connret = connect(mUrl, mWidth, mHeight, mFrameRate);
        if (!mStarted.get()) {
            if (connret) {
                doDisconnect();
            }
            return false;
        }
        return connret;
    }

    public boolean isStarted(){
        return mStarted.get();
    }

    public void stop() {
        mStarted.set(false);
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                stopInputStream();
                doDisconnect();
            }
        });
    }

    private void postDesktopResolution() {
        List<String> list = new ArrayList<String>();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        list.add(String.format("%dx%d", dm.widthPixels, dm.heightPixels));
        new StartMonitorEvent(list).post();
    }

    private void startInputStream(int type) {
//        Log4jLogger.d(TAG,"librtmp start inputstream:" + type);
        if (mType != type) {
            stopInputStream();
        }
        this.mType = type;
        switch (type) {
            case TYPE_CAMERA_FRONT:
            case TYPE_CAMERA_BACK:
//                NativeVideoCapture.startCameraCapture(mWidth, mHeight, mFrameRate, mBitRate, sSendCallback);
                FloatCamera.show(mContext, type, mWidth, mHeight, mFrameRate, mBitRate, mQuality, sSendCallback);
                return;
            case TYPE_SCREEN:
                notifyStart();
                postDesktopResolution();
        }
    }

    private void stopInputStream() {
//        Log4jLogger.d(TAG,"librtmp stop inputstream:" + mType);
        switch (mType) {
            case TYPE_NONE:
                return;
            case TYPE_SCREEN:
//                Log4jLogger.d(TAG,"stop screencast");
                notifyStop();
                break;
            case TYPE_CAMERA_BACK:
            case TYPE_CAMERA_FRONT:
//                    NativeVideoCapture.JNIStopVideoCapture();
//                Log4jLogger.d(TAG,"stop camera");
                FloatCamera.hide();
                notifyStop();
                break;
        }
        mType = TYPE_NONE;
    }

    private void notifyStart() {
        if (sysVersion >= Build.VERSION_CODES.LOLLIPOP) {
            ScreencastActivity.getInstance().start(mWidth, mHeight, mFrameRate, mBitRate, sSendCallback);
        }// else {
        if (sScreenCaptureCallbacks.size() > 0) {
            for (ScreenCaptureCallback screenCaptureCallback : sScreenCaptureCallbacks) {
                screenCaptureCallback.start(mWidth, mHeight, mFrameRate, mBitRate, this);
            }
        }
//        }
    }

    private void notifyStop() {
        if (sysVersion >= Build.VERSION_CODES.LOLLIPOP) {
            ScreencastActivity.getInstance().stop(sSendCallback);
        } //else {
        if (sScreenCaptureCallbacks.size() > 0) {
            for (ScreenCaptureCallback screenCaptureCallback : sScreenCaptureCallbacks) {
                screenCaptureCallback.stop();
            }
        }
//        }
    }

    private void doDisconnect() {
//        Log4jLogger.d(TAG, "rtmp stop");
        try {
            librtmp.native_close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public long sendVideoStream(byte[] data, int length) {
        if (!mStarted.get()) {
            return 0;
        }
        long time = System.currentTimeMillis();
        boolean ret = sendH264Packet(data, length);
//        Log4jLogger.d(TAG, String.format("rtmp send video = %d, time = %d , ret = %b", length, System.currentTimeMillis() - time, ret));
        if (!ret) {
            mExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() - mLastRestartTimestampMs > 5000 && mStarted.get()) {
                        mLastRestartTimestampMs = System.currentTimeMillis();
                        doDisconnect();
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        doConnect();
                    }
                }
            });
        }
        return 0;
    }

    private boolean connect(String url, int width, int height, int frameRate) {
//        Log4jLogger.d(TAG, String.format("librtmp connect, url = %s, width = %d, height = %d, frameRate = %d", url, width, height, frameRate));
        if (mRtmpConnected.get()) {
//            Log4jLogger.d(TAG,"rtmp is connected,close rtmp");
            native_close();
        }
        mConnecting.set(true);
//        try {
//            if (fos != null) {
//                fos.close();
//            }
//            fos = new FileOutputStream(new File("/sdcard/rtmptest/output.mp4"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        boolean ret = native_connect(url, width, height, frameRate);
        mConnecting.set(false);
        if (ret) {
//            Log4jLogger.d(TAG, String.format("connect to %s success", mUrl));
        } else {
//            Log4jLogger.e(TAG, String.format("connect to %s failed", mUrl));
        }
        mRtmpConnected.set(ret);
        return ret;
    }

    private void close() {
//        Log4jLogger.d(TAG, "librtmp close");
//        try {
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        native_close();
    }

    private boolean sendH264Packet(byte[] data, int len) {
//        if (fos != null) {
//            try {
//                fos.write(data, 0, len);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        return native_sendH264_packet(data, len);
    }
}
