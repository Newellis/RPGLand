package com.tynellis.Entities.NPC.AiTasks;

import com.tynellis.Entities.Entity;
import com.tynellis.Entities.Player;
import com.tynellis.World.World;

import java.io.Serializable;
import java.util.ArrayList;

public class FaceClosestPlayerAi implements AiTask, Serializable {
    int r;

    public FaceClosestPlayerAi(int r) {
        this.r = r;
    }

    public boolean performTask(World world, Entity e) {
        ArrayList<Entity> entities = world.getEntitiesNearEntity(e, r);
        ArrayList<Player> players = new ArrayList<Player>();
        Player closest;
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }
        if (players.size() == 1) {
            closest = players.get(0);
            double face = facingEntity(e, closest);
            if (face != e.getFacing()) {
                e.setFacing(face);
                return true;
            }
            return false;
        } else if (players.size() > 1) {
            for (int i = 0; i <= r; i++) {
                ArrayList<Entity> testEntities = world.getEntitiesNearEntity(e, i);
                for (Entity entity : testEntities) {
                    if (entity instanceof Player) {
                        closest = (Player) entity;
                        double face = facingEntity(e, closest);
                        if (face != e.getFacing()) {
                            e.setFacing(face);
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
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
