package com.tynellis.World.Entities.Rocks;

import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.metal.Nuggets.CopperNug;
import com.tynellis.World.Items.Materials.metal.ores.Copper;

import java.util.Random;

public class CopperOre extends Rock {
    public CopperOre(double x, double y, double z, Random rand) {
        super(x, y, z, rand);
        inventory.addItemPile(new ItemPile(new Copper(rand), rand.nextInt(5)));
        inventory.addItemPile(new ItemPile(new CopperNug(), rand.nextInt(3)));
    }
}
