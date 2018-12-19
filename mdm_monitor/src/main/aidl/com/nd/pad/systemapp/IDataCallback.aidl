// IDataCallback.aidl
package com.nd.pad.systemapp;

// Declare any non-default types here with import statements

interface IDataCallback {
    void onCallback(in byte[] data,int pos);
}
