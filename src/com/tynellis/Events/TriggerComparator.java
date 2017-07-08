package com.tynellis.Events;

import java.util.Comparator;

public class TriggerComparator implements Comparator<Trigger> {

    @Override
    public int compare(Trigger trigger, Trigger t1) {
        //return trigger.executeAtTurn() - t1.executeAtTurn();
        return 1;
    }
}
