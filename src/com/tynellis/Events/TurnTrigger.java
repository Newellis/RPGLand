package com.tynellis.Events;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class TurnTrigger implements Trigger {
    private Event event;
    private long start;
    private long duration;

    public TurnTrigger(Event event) {
        this.event = event;
        start = System.currentTimeMillis();
        duration = 10;
    }

    public TurnTrigger(Event event, int wait) {
        this.duration = wait;
        start = System.currentTimeMillis();
        this.event = event;
    }

    @Override
    public boolean shouldRun(int turn) {
        return start + duration <= turn;
    }

    @Override
    public void trigger(EventHandler handler) {
        event.run(handler);
    }

    @Override
    public long executeAtTurn() {
        return start + duration;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long remaining = unit.convert(executeAtTurn() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        return remaining;
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.start + duration < ((TurnTrigger) o).start + ((TurnTrigger) o).duration) {
            return -1;
        }
        if (this.start + duration > ((TurnTrigger) o).start + ((TurnTrigger) o).duration) {
            return 1;
        }
        return 0;
    }
}
