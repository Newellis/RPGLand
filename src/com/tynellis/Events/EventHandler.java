package com.tynellis.Events;

import com.tynellis.threadManager.SynchronizingManager;
import com.tynellis.threadManager.TriggerAction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

public class EventHandler {
    //private SortedSet<Trigger> triggers = new TreeSet<Trigger>(new TriggerComparator());
    //private ArrayList<Trigger> triggers = new ArrayList<Trigger>();
    private BlockingQueue<Trigger> triggers = new DelayQueue<Trigger>();

    public synchronized void tick() {
        //for (Trigger trigger: triggers) {
        System.out.println("start tick with Queue of size " + triggers.size());
        int entitiesTicked = 0;
        Trigger trigger = triggers.poll();
        while (trigger != null) {
            //trigger.trigger(this);
            SynchronizingManager.action(new TriggerAction(this, trigger));
            entitiesTicked++;
            trigger = triggers.poll();
        }
        System.out.println("end of tick with " + entitiesTicked + " entities updated");
    }

    public void addEvent(Trigger trigger) {
        triggers.add(trigger);
    }
}
