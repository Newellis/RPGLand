package com.tynellis.Events;

public class TurnTrigger implements Trigger {
    private Event event;
    private int turn;

    public TurnTrigger(Event event, int turn) {
        this.turn = turn;
        this.event = event;
    }

    @Override
    public boolean shouldRun(int turn) {
        return this.turn <= turn;
    }

    @Override
    public void trigger(EventHandler handler) {
        event.run(handler);
    }

    @Override
    public int executeAtTurn() {
        return turn;
    }
}
