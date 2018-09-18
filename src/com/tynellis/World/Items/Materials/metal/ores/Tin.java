package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.TinNug;
import com.tynellis.World.Items.Materials.metal.Smeltable;

import java.util.Random;

public class Tin extends Smeltable {
    public Tin(Random random) {
        super("Tin Ore", 4, 200, 0.01, 0.90, 10, 0, 3, random);
    }

    @Override
    protected Item getCooked() {
        if (purity > 0.8) {
            return new TinNug();
        }
        cookingTime = cookTime;
        return this;
    }
}
