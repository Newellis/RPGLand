package com.tynellis.Entities.NPC.AiTasks;

import com.tynellis.Entities.Entity;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.World;

import java.util.ArrayList;

public class FollowEntityAi extends PathfinderAi implements AiTask {
    private int minRange;
    private Class entityType;
    private Entity closest;
    private boolean lock = false;

    public FollowEntityAi(Class type, int range, int minRange) {
        super(range);
        this.minRange = minRange;
        entityType = type;
    }

    public FollowEntityAi(Entity guide, int range, int minRange) {
        super(range);
        this.minRange = minRange;
        closest = guide;
        lock = true;
    }

    @Override
    public boolean performTask(World world, Entity e) {
        if (findTarget(world, e)) {
            setLocation(Math.round(closest.getX()), Math.round(closest.getY()) + 0.5, closest.getZ());
            if (heuristicCostEstimate(new Node(e.getX(), e.getY(), e.getZ()), new Node(destX, destY, destZ)) > minRange) {
                return super.performTask(world, e);
            } else {
                e.setMoving(false);
                path.clear();
                return false;
            }
        }
        return false;
    }

    public boolean findTarget(World world, Entity e) {
        if (lock && closest != null) {
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
