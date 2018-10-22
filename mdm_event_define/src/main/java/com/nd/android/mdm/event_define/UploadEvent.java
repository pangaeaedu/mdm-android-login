package com.nd.android.mdm.event_define;

public class UploadEvent extends Event {
    private String xpath;
    private String fileInfo;
    private int timeout;

    public UploadEvent(String path) {
        this(path, "", 0);
    }

    public UploadEvent(String path, String fileInfo) {
        this(path, fileInfo, 0);
    }

    public UploadEvent(String path, String fileInfo, int timeout) {
        this.xpath = path;
        this.fileInfo = fileInfo;
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return xpath;
    }

    public String getPath() {
        return xpath;
    }

    public String getFileInfo() {
        return fileInfo;
    }

    public int getTimeout(){
        return timeout;
    }
}
