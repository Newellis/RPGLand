package com.tynellis.Events;

public interface Trigger {
    boolean shouldRun(int turn);
    void trigger(EventHandler handler);
    int executeAtTurn();
}
