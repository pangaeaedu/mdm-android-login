package com.nd.pad.systemapp;
import android.content.Intent;
import com.nd.pad.systemapp.IDataCallback;
import android.view.MotionEvent;
interface ISystemControl{
	String runCmd(String arg);
	int invoke(String method,in Intent intent);
	int getVersion();
	void setDataCallback(in IDataCallback callback);
	void sendMotionEvent(long downTime, long eventTime, int action, float x, float y, int metaState);
	String invokeMethod(String method,in Intent intent);
}