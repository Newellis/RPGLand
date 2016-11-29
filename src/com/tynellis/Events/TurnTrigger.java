package com.tynellis.Events;

public class TurnTrigger implements Trigger {
    private Event event;
    private int turn;
    private int order;

    public TurnTrigger(Event event) {
        this.event = event;
        turn = 0;
    }
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
