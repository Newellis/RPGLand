package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.NPC.AiTasks.AiTask;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.PathfinderAi;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.World;

import java.util.ArrayList;

public class FollowEntityAi extends AiTask {
    private Class entityType;
    protected Entity closest;
    protected int range, minRange;

    public FollowEntityAi(Class type, int range, int minRange) {
        this.range = range;
        this.minRange = minRange;
        entityType = type;
    }

    public FollowEntityAi(Entity guide, int range, int minRange) {
        this.range = range;
        this.minRange = minRange;
        closest = guide;
        entityType = guide.getClass();
    }

    @Override
    public boolean performTask(World world, NpcBase entity) {
        PathfinderAi pathfinder = entity.getPathfinder();
        if (findTarget(world, entity)) {
            double x = Math.round(closest.getX()),
                    y = Math.round(closest.getY()),
                    z = Math.round(closest.getZ());
            if (pathfinder.getCurrentActivity() != this) {
                pathfinder.setCurrentActivity(this);
                pathfinder.setRanges(range, minRange);
            }
            if (pathfinder.heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(x, y, z)) > minRange) {
                pathfinder.setLocation(x, y, z);
                return pathfinder.performTask(world, entity);
            } else {
                entity.setMoving(false);
                return false;
            }
        }
        entity.setMoving(false);
        return false;
    }

    @Override
    public boolean isFinished(NpcBase entity) {
        PathfinderAi pathfinder = entity.getPathfinder();
        return closest == null || closest.isDead() || pathfinder.getPath().size() <= 0 || pathfinder.getPathLength() < minRange;
    }

    public boolean findTarget(World world, NpcBase e) {
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