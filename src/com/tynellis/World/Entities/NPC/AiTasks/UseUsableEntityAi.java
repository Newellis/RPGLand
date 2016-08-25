package com.tynellis.World.Entities.NPC.AiTasks;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.FollowEntityAi;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.PathfinderAi;
import com.tynellis.World.Entities.UsableEntity.UsableEntity;
import com.tynellis.World.World;

import java.io.Serializable;

public abstract class UseUsableEntityAi extends AiTask implements Serializable {
    protected PathfinderAi pathfinder;
    private FaceClosestAi faceClosest;
    protected UsableEntity tool;
    protected Class toolType;

    public UseUsableEntityAi(Class<UsableEntity> type, int range) {
        pathfinder = new FollowEntityAi(type, range, 1);
        faceClosest = new FaceClosestAi(type, 1);
        toolType = type;
    }

    public UseUsableEntityAi(UsableEntity entity, int range) {
        pathfinder = new FollowEntityAi(entity, range, 1);
        tool = entity;
        toolType = entity.getClass();
        faceClosest = new FaceClosestAi(toolType, 1);
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

    protected abstract boolean shouldUse(World world, KillableEntity entity);

    protected boolean using(Object o, KillableEntity entity) {
        return false;
    }
}
