package com.nd.android.mdm.event_define;






import com.nd.screen.event.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaoyue1019 on 12-28.
 */

public class MockEvent extends Event {
    public List<MotionEvent> motionEvents;

    public MockEvent(MotionEvent[] events) {
        motionEvents = new ArrayList<>();
        for (MotionEvent event : events) {
            motionEvents.add(event);
        }
    }

    public MockEvent(List<MotionEvent> events) {
        this.motionEvents = events;
    }
}
