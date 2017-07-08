package com.tynellis.Events;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

public class EventHandler {
    private int turn = 0;
    //private SortedSet<Trigger> triggers = new TreeSet<Trigger>(new TriggerComparator());
    //private ArrayList<Trigger> triggers = new ArrayList<Trigger>();
    private BlockingQueue<Trigger> triggers = new DelayQueue<Trigger>();
    private boolean canBlock = true;

    public synchronized void tick() {
        turn++;
        //for (Trigger trigger: triggers) {
        System.out.println("start tick with Queue of size " + triggers.size());
        int entitiesTicked = 0;
        Trigger trigger = triggers.poll();
        while (trigger != null) {
            block();
            trigger.trigger(this);
            entitiesTicked++;
            release();
            trigger = triggers.poll();
        }
        System.out.println("end of tick with " + entitiesTicked + " entities updated");
    }

    public void addEvent(Trigger trigger) {
        triggers.add(trigger);
    }

    public synchronized void block() {
        while (!canBlock) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        canBlock = false;
        notifyAll();
    }

    public synchronized void release() {
        canBlock = true;
        notifyAll();
    }
}
