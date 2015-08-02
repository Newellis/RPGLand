package com.tynellis.Entities;

import java.util.Comparator;

public class EntityComparator implements Comparator<Entity>{
    @Override
    public int compare(Entity t0, Entity t1) {
        if (t1.getY() < t0.getY()){
            return 1;
        } else if (t1.getY() > t0.getY()){
            return -1;
        } else if (t1.getX() < t0.getX()){
                return 1;
        } else if (t1.getX() > t0.getX()){
            return -1;
        }
        return 0;
    }
}
