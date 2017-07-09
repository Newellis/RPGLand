package com.tynellis.threadManager;

import com.tynellis.Events.EventHandler;
import com.tynellis.Events.Trigger;

public class TriggerAction implements Runnable {
    private EventHandler handler;
    private Trigger trigger;

    public TriggerAction(EventHandler handler, Trigger trigger) {
        this.handler = handler;
        this.trigger = trigger;
    }

    @Override
    public void run() {
        trigger.trigger(handler);
    }
}
