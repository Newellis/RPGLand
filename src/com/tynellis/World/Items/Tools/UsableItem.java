package com.tynellis.World.Items.Tools;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Items.Item;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public abstract class UsableItem extends Item {
    public UsableItem(String name, int maxStackSize, int artRow, int artCol) {
        super(name, maxStackSize, artRow, artCol);
    }

    public abstract boolean use(Region region, Random random, KillableEntity user);

    public abstract boolean canUse(Region region, KillableEntity user);
}
