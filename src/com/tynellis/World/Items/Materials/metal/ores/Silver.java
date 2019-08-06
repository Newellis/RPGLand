package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.SilverNug;
import com.tynellis.World.Items.Materials.metal.Smeltable;

import java.util.Random;

public class Silver extends Smeltable {
    public Silver(Random random) {
        super("Silver Ore", 9, 400, 0.01, 0.9, 10, 0, 5, random);
    }

    @Override
    protected Item getCooked() {
        if (purity > 0.8) {
            return new SilverNug(purity);
        }
        cookingTime = cookTime;
        return this;
    }
}
