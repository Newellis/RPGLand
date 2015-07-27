package com.tynellis.Events;

import java.util.SortedSet;
import java.util.TreeSet;

public class EventHandler {
    private int turn = 0;
    private SortedSet<Trigger> triggers = new TreeSet<Trigger>(new TriggerComparator());

    public void tick() {
        turn++;
        while (triggers.size() > 0) {
            Trigger trigger = triggers.first();
            if (!trigger.shouldRun(turn)) {
                break;
            }
            trigger.trigger(this);
            triggers.remove(trigger);
        }
    }

    public void addEvent(Trigger trigger) {
        triggers.add(trigger);
    }

    public int getCurrentTurn() {
        return turn;
    }

    public static void main(String[] args){
        EventHandler handler = new EventHandler();
        for (int i = 0; i < 110; i++){
            handler.tick();
        }
    }
}
