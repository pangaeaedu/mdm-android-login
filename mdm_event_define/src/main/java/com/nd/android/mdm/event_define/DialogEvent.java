package com.nd.android.mdm.event_define;

import de.greenrobot.event.EventBus;

public class DialogEvent extends Event {
    public static final String HIDE_DIALOG = "";
    private String content;
    private String title;

    public DialogEvent(String text) {
        content = text;
    }

    public DialogEvent(String title, String content) {
        this.content = content;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getTitle(){
        return title;
    }

    public void show() {
        EventBus.getDefault().post(this);
    }

    @Override
    public String toString() {
        return content;
    }

}
