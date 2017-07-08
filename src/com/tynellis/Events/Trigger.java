package com.tynellis.Events;

import java.util.concurrent.Delayed;

public interface Trigger extends Delayed {
    boolean shouldRun(int turn);
    void trigger(EventHandler handler);

    long executeAtTurn();
}
