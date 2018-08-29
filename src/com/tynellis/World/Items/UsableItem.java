package com.tynellis.World.Items;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.World;

public abstract class UsableItem extends Item {
    public UsableItem(String name, int maxStackSize, int artRow, int artCol) {
        super(name, maxStackSize, artRow, artCol);
    }

    public abstract boolean use(World world, KillableEntity user);

    public abstract boolean canUse(World world, KillableEntity user);
}
