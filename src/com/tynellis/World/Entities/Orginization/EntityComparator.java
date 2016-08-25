package com.tynellis.World.Entities.Orginization;

import com.tynellis.World.Entities.Entity;

import java.util.Comparator;

public class EntityComparator implements Comparator<Entity> {
    @Override
    public int compare(Entity t0, Entity t1) {
        if (t1.getY() - t1.getZ() > t0.getY() - t0.getZ()) {
            return -1;
        } else if (t1.getY() - t1.getZ() < t0.getY() - t0.getZ()) {
            return 1;
        } else if (t1.getZ() < t0.getZ()) {
            return 1;
        } else if (t1.getZ() > t0.getZ()) {
            return -1;
        } else if (t1.getX() < t0.getX()) {
            return 1;
        } else if (t1.getX() > t0.getX()) {
            return -1;
        }
        int alpabetical = t0.getClass().getName().compareTo(t1.getClass().getName());
        if (alpabetical == 0) {
            alpabetical = t0.compareTo(t1);
        }
        return alpabetical;
    }
}
