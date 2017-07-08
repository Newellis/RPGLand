package com.tynellis.Events;

import java.util.ArrayList;

public class EventHandler {
    private int turn = 0;
    //private SortedSet<Trigger> triggers = new TreeSet<Trigger>(new TriggerComparator());
    private ArrayList<Trigger> triggers = new ArrayList<Trigger>();//todo change into a "queue" of lists to keep track of order

    public void tick() {
        turn++;
        //for (Trigger trigger: triggers) {
        while (triggers.size() > 0) {
            Trigger trigger = triggers.get(0); //assumes triggers added in order
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
