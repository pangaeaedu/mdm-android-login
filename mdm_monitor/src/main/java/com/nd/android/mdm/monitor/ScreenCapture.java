package com.nd.android.mdm.monitor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.Base64;
import android.view.Surface;

import com.nd.android.adhoc.basic.log.Logger;
import com.nd.eci.sdk.log.Log4jLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by yaoyue1019 on 2018/1/10.
 */

public class ScreenCapture {
    private static final String TAG = "Screenshot";
    private static ScreenCapture instance;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mProjectionManager;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;

    private int mWidth;
    private int mHeight;
    private int mDpi;

    private boolean mPermissionRequested = false;
    private Intent requestResultData;
    private AtomicBoolean mStart = new AtomicBoolean(false);

    private Context mContext;

    public void init(Context context, int width, int height, int dpi) {
        mContext = context.getApplicationContext();
        mWidth = width;
        mHeight = height;
        mDpi = dpi;
        mProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public int getScreenshotWidth() {
        return mWidth;
    }

    public int getScreenHeight() {
        return mHeight;
    }

    public boolean isReady() {
        return mStart.get();
    }

    public void startScreenshot() {
        if (!mStart.compareAndSet(false, true)) {
            return;
        }
        setUpMediaProjection();
        setUpVirtualDisplay();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScreenshot() {
        if (mVirtualDisplay != null) {
            Surface surface = mVirtualDisplay.getSurface();
            mVirtualDisplay.release();
            if(surface != null){
                surface.release();
            }
            mVirtualDisplay = null;
        }


        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }

        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }

        mStart.set(false);
    }

    public synchronized static ScreenCapture getInstance() {
        if (instance == null) {
            instance = new ScreenCapture();
        }
        return instance;
    }

    public Bitmap getScreenshot() {
        return getScreenshot(mImageReader);
    }

    private Bitmap getScreenshot(ImageReader imageReader) {
        if (imageReader != null) {
            Bitmap bitmap = null;
            Image image = null;
            int count = 0;
            while (count <5){
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                image = imageReader.acquireNextImage();
                if(image != null){
                    break;
                }

                count++;
            }
            Logger.w(TAG,"success count = "+count);
            if (image != null) {
                int width = image.getWidth();
                int height = image.getHeight();
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                image.close();
            }
            return bitmap;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpVirtualDisplay() {
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 1);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(mContext.getPackageName() + "-screenshot", mWidth, mHeight, mDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection() {
        mMediaProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, requestResultData);
    }

//    private void requestPermission(Context context) {
//        Intent intent = new Intent(context, ScreenshotRequestPermissionActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//    }
    public void setRequestData(Intent data){
        requestResultData = data;
    }

//    public void onEvent(RequestScreenshotPermissionEvent event) {
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//        if (event.success) {
//            mPermissionRequested = true;
//            requestResultData = event.data;
//            if (mStart.compareAndSet(false, true)) {
//                setUpMediaProjection();
//                setUpVirtualDisplay();
//            }
//        }
//
//        new ScreenshotReadyEvent(true).post();
//    }

    public static String bitmap2Base64(Bitmap bitmap) {
        String bitstr = null;
        if (bitmap == null) {
            return bitstr;
        } else {
            ByteArrayOutputStream bStream = null;

            try {
                bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] bytes = bStream.toByteArray();
                bitstr = Base64.encodeToString(bytes, 0);
            } catch (Exception var12) {
                Log4jLogger.w("BitMapUtil", "" + var12.getMessage());
            } finally {
                if (bStream != null) {
                    try {
                        bStream.close();
                    } catch (IOException var11) {
                        Log4jLogger.w("BitMapUtil", "" + var11.getMessage());
                    }
                }

            }
            return bitstr;
        }
    }
}