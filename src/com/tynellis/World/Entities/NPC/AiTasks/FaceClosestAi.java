package com.tynellis.World.Entities.NPC.AiTasks;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.World;

import java.io.Serializable;
import java.util.ArrayList;

public class FaceClosestAi extends AiTask implements Serializable {
    private Class entityType;
    double r;

    public FaceClosestAi(Class type, double r) {
        this.r = r;
        entityType = type;
    }

    public boolean performTask(World world, NpcBase mob) {
        ArrayList<Entity> entities = world.getEntitiesNearEntity(mob, r);
        ArrayList<Entity> entitiesOfType = new ArrayList<Entity>();
        Entity closest;
        for (Entity entity : entities) {
            if (entityType.isInstance(entity)) {
                entitiesOfType.add(entity);
            }
        }
        if (entitiesOfType.size() == 1) {
            closest = entitiesOfType.get(0);
            double face = facingEntity(mob, closest);
            if (face != mob.getFacing()) {
                mob.setLooking(face);
                mob.setMoving(false);
                return true;
            }
            return false;
        } else if (entitiesOfType.size() > 1) {
            for (int i = 0; i <= r; i++) {
                ArrayList<Entity> testEntities = world.getEntitiesNearEntity(mob, i);
                for (Entity entity : testEntities) {
                    if (entityType.isInstance(entity)) {
                        closest = entity;
                        double face = facingEntity(mob, closest);
                        if (face != mob.getFacing()) {
                            mob.setLooking(face);
                            mob.setMoving(false);
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isFinished(NpcBase entity) {
        return true;
    }

    private double facingEntity(Entity e, Entity facing) {
        return facingPoint(e, facing.getX(), facing.getY());
    }

    public static double facingPoint(Entity e, double X, double Y) {
        double angle = Math.atan2(Y - e.getY(), X - e.getX()) - Math.atan2(-100, -100);
        if (angle < 0) {
            angle = angle + 2 * Math.PI;
        }

        double facing = Math.abs((2 * (angle / Math.PI)) - 5);
        if (facing >= 4) {
            facing -= 4;
        }
        return facing;
    }
}
