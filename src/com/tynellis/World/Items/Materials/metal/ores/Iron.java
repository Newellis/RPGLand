package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.IronNug;
import com.tynellis.World.Items.Materials.metal.Smeltable;

import java.util.Random;

public class Iron extends Smeltable {
    public Iron(Random random) {
        super("Iron Ore", 13, 600, 0.98, 0.5, 10, 0, 6, random);
    }

    @Override
    protected Item getCooked() {
        if (purity > 0.6) {
            return new IronNug();
        }
        cookingTime = cookTime;
        return this;
    }
}
