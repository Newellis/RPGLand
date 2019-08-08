package com.tynellis.World.Entities.Living.Ai.Pathfinding.toEntity;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Entities.UsableEntity.Chest;
import com.tynellis.World.Entities.UsableEntity.UsableEntity;
import com.tynellis.World.Entities.UsableEntity.using_interfaces.ChestInterface;
import com.tynellis.World.Entities.UsableEntity.using_interfaces.UsingInterface;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.Containers.Filters.ItemFilter;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.world_parts.Regions.Region;

public class UseChestAi extends UseUsableEntityAi {
    private ItemFilter filter;
    private boolean using = false;
    private int delay = 0;

    public UseChestAi(Class<UsableEntity> type, ItemFilter filter, int range) {
        super(type, range);
        this.filter = filter;
    }

    public UseChestAi(UsableEntity entity, ItemFilter filter, int range) {
        super(entity, range);
        this.filter = filter;
    }

    @Override
    protected boolean shouldUse(Region region, LivingEntity entity) {
        if (findTarget(region, entity)) {
            Container inventory = entity.getInventory();
            for (int i = 0; i < inventory.getInventory().length; i++) {
                ItemPile pile = inventory.getInventory()[i].getPile();
                if (pile != null && filter.followsFilter(pile) && !((Chest) tool).isFull()) {
                    return true;
                }
            }
        }
        using = false;
        return false;
    }

    @Override
    protected boolean using(UsingInterface usingInterface, KillableEntity entity) {
        if (usingInterface instanceof ChestInterface) {
            Container inventory = entity.getInventory();
            for (int i = 0; i < inventory.getInventory().length; i++) {
                if (inventory.getInventory()[i] != null) {
                    ItemPile pile = inventory.getInventory()[i].getPile();
                    if (delay <= 0 && pile != null && ((ChestInterface) usingInterface).canAddItem(pile) && filter.followsFilter(pile)) {
                        ((ChestInterface) usingInterface).addItem(pile);
                        if (pile.getSize() <= 0) {
                            inventory.getInventory()[i].setPile(null);
                        }
                        using = true;
                        delay = 30;
                        System.out.println("inventory: " + inventory + " chest: " + usingInterface);
                        usingInterface.stopUsing();
                        return true;
                    }
                }
            }
            using = delay != 0;
            delay--;
            usingInterface.stopUsing();
        }
        return using;
    }

    @Override
    public boolean isFinished(LivingEntity entity) {
        return !using;
    }
}
