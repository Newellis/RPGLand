package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.GoldNug;
import com.tynellis.World.Items.Materials.metal.Smeltable;

import java.util.Random;

public class Gold extends Smeltable {
    public Gold(Random random) {
        super("Gold Ore", 10, 300, 0.05, 0.05, 10, 0, 4, random);
    }

    @Override
    protected Item getCooked() {
        if (purity > 0.8) {
            return new GoldNug();
        }
        cookingTime = cookTime;
        return this;
    }
}
