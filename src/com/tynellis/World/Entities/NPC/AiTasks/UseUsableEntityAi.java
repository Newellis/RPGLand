package com.tynellis.World.Entities.NPC.AiTasks;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.FollowEntityAi;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.PathfinderAi;
import com.tynellis.World.Entities.UsableEntity.UsableEntity;
import com.tynellis.World.World;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class UseUsableEntityAi extends AiTask implements Serializable {
    public PathfinderAi pathfinder;
    private FaceClosestAi faceClosest;
    protected UsableEntity tool;
    protected Class toolType;
    private int range;

    public UseUsableEntityAi(Class<UsableEntity> type, int range) {
        pathfinder = new FollowEntityAi(type, range, 1);
        faceClosest = new FaceClosestAi(type, 2);
        toolType = type;
        this.range = range;
    }

    public UseUsableEntityAi(UsableEntity entity, int range) {
        pathfinder = new FollowEntityAi(entity, range, 1);
        tool = entity;
        toolType = entity.getClass();
        faceClosest = new FaceClosestAi(toolType, 1);
        this.range = range;
    }

    @Override
    public boolean performTask(World world, KillableEntity entity) {
        if (shouldUse(world, entity)) {
            boolean moving = pathfinder.performTask(world, entity);
            if (pathfinder.getPathLength() < 2) {
                faceClosest.performTask(world, entity);
                return using(tool.use(entity), entity);
            }
            return moving;
        }
        return false;
    }

    protected boolean findTarget(World world, KillableEntity e) {
        if (tool != null && !tool.isDead()) {
            return true;
        }
        ArrayList<Entity> entities = world.getEntitiesNearEntity(e, range);
        ArrayList<UsableEntity> closestType = new ArrayList<UsableEntity>();
        for (Entity entity : entities) {
            if (toolType.isInstance(entity)) {
                closestType.add((UsableEntity) entity);
            }
        }
        if (closestType.size() == 1) {
            tool = closestType.get(0);
            return true;
        } else if (closestType.size() > 1) {
            for (int i = 0; i <= range; i++) {
                ArrayList<Entity> testEntities = world.getEntitiesNearEntity(e, i);
                for (Entity entity : testEntities) {
                    if (toolType.isInstance(entity)) {
                        tool = (UsableEntity) entity;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected abstract boolean shouldUse(World world, KillableEntity entity);

    protected boolean using(Object o, KillableEntity entity) {
        return false;
    }
}
