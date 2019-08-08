package com.tynellis.World.Entities.Living.Ai.Pathfinding.toEntity;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.Living.Ai.FaceClosestAi;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Entities.UsableEntity.UsableEntity;
import com.tynellis.World.Entities.UsableEntity.using_interfaces.UsingInterface;
import com.tynellis.World.world_parts.Regions.Region;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public abstract class UseUsableEntityAi extends FollowEntityAi implements Serializable {
    //public FollowEntityAi pathfinder;
    private FaceClosestAi faceClosest;
    protected UsableEntity tool;
    protected Class toolType;
    private int range;

    public UseUsableEntityAi(Class<UsableEntity> type, int range) {
        super(type, range, 1);
        faceClosest = new FaceClosestAi(type, 2);
        toolType = type;
        this.range = range;
    }

    public UseUsableEntityAi(UsableEntity entity, int range) {
        super(entity, range, 1);
        tool = entity;
        toolType = entity.getClass();
        faceClosest = new FaceClosestAi(toolType, 1);
        this.range = range;
    }

    @Override
    public boolean performTask(Region region, Random random, LivingEntity entity) {
        if (shouldUse(region, entity)) {
            boolean moving = super.performTask(region, random, entity);
            if (tool.canBeUsedBy(entity)) {
                faceClosest.performTask(region, random, entity);
                boolean using = using(tool.use(region, entity), entity);
                return using;
            }
            return moving;
        }
        return false;
    }

    public boolean findTarget(Region region, LivingEntity e) {
        if (tool != null && !tool.isDead()) {
            return true;
        }
        ArrayList<Entity> entities = region.getEntitiesNearEntity(e, range);
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
                ArrayList<Entity> testEntities = region.getEntitiesNearEntity(e, i);
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

    protected abstract boolean shouldUse(Region region, LivingEntity entity);

    protected boolean using(UsingInterface o, KillableEntity entity) {
        return false;
    }
}
