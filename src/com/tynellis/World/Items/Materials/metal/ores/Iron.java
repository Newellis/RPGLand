package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.IronNug;
import com.tynellis.World.Items.Smeltable;

public class Iron extends Smeltable {
    public Iron() {
        super("Iron Ore", 13, 600, 10, 0, 6);
    }

    @Override
    protected Item getCooked() {
        return new IronNug();
    }
}
