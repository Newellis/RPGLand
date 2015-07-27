package com.tynellis.Events;

public abstract class TickEvent implements Event{
    @Override
    public void run(EventHandler handler) {
        handler.addEvent(new TurnTrigger(this, handler.getCurrentTurn() + 1));
    }
}
