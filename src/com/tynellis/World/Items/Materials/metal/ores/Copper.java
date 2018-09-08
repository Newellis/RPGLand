package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.CopperNug;
import com.tynellis.World.Items.Smeltable;

public class Copper extends Smeltable {
    public Copper() {
        super("Copper Ore", 10, 650, 10, 0, 2);
    }

    @Override
    protected Item getCooked() {
        return new CopperNug();
    }
}
