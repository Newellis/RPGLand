package com.tynellis.World.Entities.Living.Ai.Pathfinding.toEntity;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CollectItemsFromEntityAi extends AttackEntityAi {
    private CollectItemsAi gatherItems;
    protected transient List<Node> path = new ArrayList<Node>();

    public CollectItemsFromEntityAi(Class type, int range, int minRange) {
        super(type, range, minRange);
        gatherItems = new CollectItemsAi(range);
    }

    public boolean performTask(Region region, Random random, LivingEntity entity) {
        if (entity.getInventory().isFull()) {
            entity.setMoving(false);
            return false;
        }
        if (!gatherItems.performTask(region, random, entity)) {
            if (path != null) {
                entity.getPathfinder().setCurrentActivity(this);
                entity.getPathfinder().setPath(path);
            }
            boolean attack = super.performTask(region, random, entity);
            path = (attack) ? entity.getPathfinder().getPath() : null;

            return attack;
        }
        return true;
    }

    public boolean findTarget(Region region, LivingEntity e) {
        boolean foundTarget = super.findTarget(region, e);

        if (foundTarget) {
            for (ItemPile item : ((KillableEntity) closest).getInventory().getContents()) {
                if (e.getInventory().canAddItem(item)) {
                    return true;
                }
            }
        }
        return false;
    }
}
