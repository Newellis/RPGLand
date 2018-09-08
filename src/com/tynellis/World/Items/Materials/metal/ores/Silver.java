package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.SilverNug;
import com.tynellis.World.Items.Smeltable;

public class Silver extends Smeltable {
    public Silver() {
        super("Silver Ore", 9, 400, 10, 0, 5);
    }

    @Override
    protected Item getCooked() {
        return new SilverNug();
    }
}
