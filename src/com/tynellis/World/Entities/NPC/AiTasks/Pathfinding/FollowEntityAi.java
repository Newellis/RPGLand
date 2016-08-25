package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.World;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class FollowEntityAi extends PathfinderAi {
    private Class entityType;
    protected Entity closest;

    public FollowEntityAi(Class type, int range, int minRange) {
        super(range, minRange);
        entityType = type;
    }

    public FollowEntityAi(Entity guide, int range, int minRange) {
        super(range, minRange);
        closest = guide;
        entityType = guide.getClass();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    @Override
    public boolean performTask(World world, KillableEntity entity) {
        if (closest != null && getPathLength() > range) {
            closest = null;
        }
        if (findTarget(world, entity)) {
            setLocation(Math.round(closest.getX()), Math.round(closest.getY()) + 0.5, Math.round(closest.getZ()));
            if (heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(destX, destY, destZ)) > minRange) {
                return super.performTask(world, entity);
            } else {
                entity.setMoving(false);
                path.clear();
                return false;
            }
        }
        entity.setMoving(false);
        return false;
    }

    @Override
    public boolean isFinished() {
        return closest == null || closest.isDead() || path.size() <= 0 || getPathLength() < minRange;
    }

    public boolean findTarget(World world, KillableEntity e) {
        if (closest != null && !closest.isDead()) {
            return true;
        }
        ArrayList<Entity> entities = world.getEntitiesNearEntity(e, range);
        ArrayList<Entity> closestType = new ArrayList<Entity>();
        for (Entity entity : entities) {
            if (entityType.isInstance(entity)) {
                closestType.add(entity);
            }
        }
        if (closestType.size() == 1) {
            closest = closestType.get(0);
            return true;
        } else if (closestType.size() > 1) {
            for (int i = 0; i <= range; i++) {
                ArrayList<Entity> testEntities = world.getEntitiesNearEntity(e, i);
                for (Entity entity : testEntities) {
                    if (entityType.isInstance(entity)) {
                        closest = entity;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
