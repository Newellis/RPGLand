package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.ItemEntity;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.World;

import java.util.ArrayList;

public class CollectItemsAi extends FollowEntityAi {

    public CollectItemsAi(int range) {
        super(ItemEntity.class, range, 0);
    }

    public boolean performTask(World world, NpcBase entity) {
        if (entity.getInventory().isFull()) {
            entity.setMoving(false);
            return false;
        }
        if (closest != null && !entity.getInventory().canAddItem(((ItemEntity) closest).getItem())) {
            closest = null;
        }
        boolean task = super.performTask(world, entity);
        if (!task) {
            closest = null;
        }
        return task;
    }

    public boolean findTarget(World world, NpcBase mob) {
        if (closest != null && !closest.isDead()) {
            return true;
        }
        ArrayList<Entity> entities = world.getEntitiesNearEntity(mob, range);
        ArrayList<Entity> closestType = new ArrayList<Entity>();
        for (Entity entity : entities) {
            if (entity instanceof ItemEntity && mob.getInventory().canAddItem(((ItemEntity) entity).getItem())) {
                closestType.add(entity);
            }
        }
        if (closestType.size() == 1) {
            closest = closestType.get(0);
            return true;
        } else if (closestType.size() > 1) {
            for (int i = 0; i <= range; i++) {
                ArrayList<Entity> testEntities = world.getEntitiesNearEntity(mob, i);
                for (Entity entity : testEntities) {
                    if (entity instanceof ItemEntity && mob.getInventory().canAddItem(((ItemEntity) entity).getItem())) {
                        closest = entity;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
