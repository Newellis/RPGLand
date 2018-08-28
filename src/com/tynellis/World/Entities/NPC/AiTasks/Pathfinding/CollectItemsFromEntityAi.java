package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.World;

public class CollectItemsFromEntityAi extends AttackEntityAi {
    private CollectItemsAi gatherItems;

    public CollectItemsFromEntityAi(Class type, int range, int minRange) {
        super(type, range, minRange);
        gatherItems = new CollectItemsAi(range);
    }

    public boolean performTask(World world, KillableEntity entity) {
        if (entity.getInventory().isFull()) {
            entity.setMoving(false);
            return false;
        }
        if (!gatherItems.performTask(world, entity)) {
            return super.performTask(world, entity);
        }
        return true;
    }
}
