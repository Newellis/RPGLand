package com.tynellis.World.Items;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.world_parts.Region;

import java.util.Random;

public abstract class UsableItem extends Item {
    public UsableItem(String name, int maxStackSize, int artRow, int artCol) {
        super(name, maxStackSize, artRow, artCol);
    }

    public abstract boolean use(Region region, Random random, KillableEntity user);

    public abstract boolean canUse(Region region, KillableEntity user);
}
