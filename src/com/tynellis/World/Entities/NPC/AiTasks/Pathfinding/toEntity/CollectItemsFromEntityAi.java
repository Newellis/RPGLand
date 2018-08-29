package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.World;

import java.util.ArrayList;
import java.util.List;

public class CollectItemsFromEntityAi extends AttackEntityAi {
    private CollectItemsAi gatherItems;
    protected transient List<Node> path = new ArrayList<Node>();

    public CollectItemsFromEntityAi(Class type, int range, int minRange) {
        super(type, range, minRange);
        gatherItems = new CollectItemsAi(range);
    }

    public boolean performTask(World world, NpcBase entity) {
        if (entity.getInventory().isFull()) {
            entity.setMoving(false);
            return false;
        }
        if (!gatherItems.performTask(world, entity)) {
            if (path != null) {
                entity.getPathfinder().setCurrentActivity(this);
                entity.getPathfinder().setPath(path);
            }
            boolean attack = super.performTask(world, entity);

            path = (attack) ? entity.getPathfinder().getPath() : null;

            return attack;
        }
        return true;
    }
}
