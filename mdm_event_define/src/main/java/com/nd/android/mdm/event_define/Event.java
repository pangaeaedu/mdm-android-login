package com.nd.android.mdm.event_define;

import de.greenrobot.event.EventBus;

public class Event {
    public void post() {
        EventBus.getDefault().post(this);
    }
}
