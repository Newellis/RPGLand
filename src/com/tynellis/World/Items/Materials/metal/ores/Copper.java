package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.CopperNug;
import com.tynellis.World.Items.Materials.metal.Smeltable;

import java.util.Random;

public class Copper extends Smeltable {
    public Copper(Random random) {
        super("Copper Ore", 10, 650, 0.8, 0.6, 10, 0, 2, random);
    }

    @Override
    protected Item getCooked() {
        if (purity > 0.8) {
            return new CopperNug();
        }
        cookingTime = cookTime;
        return this;
    }
}
