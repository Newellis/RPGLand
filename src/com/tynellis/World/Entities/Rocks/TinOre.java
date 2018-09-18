package com.tynellis.World.Entities.Rocks;

import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.metal.ores.Tin;

import java.util.Random;

public class TinOre extends Rock {
    public TinOre(double x, double y, double z, Random rand) {
        super(x, y, z, rand);
        inventory.addItemPile(new ItemPile(new Tin(rand), rand.nextInt(4)));
    }
}
