package com.nd.android.mdm.event_define;


public class ProgressEvent extends Event {
    public static final int TYPE_BEGIN = 1;
    public static final int TYPE_PROGRESS = 2;
    public static final int TYPE_FINISH = 3;
    public static final int TYPE_ERROR = 4;
    private int value;
    private int type;
    private long sessionId;
    private int size;
    private String info;
    private String path;

    public ProgressEvent(long sessionId, int type, int value) {
        this.value = value;
        this.type = type;
        this.sessionId = sessionId;
    }

    public int getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getSessionId() {
        return sessionId;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    public String getInfo() {
        return info;
    }
}
