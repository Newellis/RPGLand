package com.tynellis.World.Entities.Rocks;

import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.ores.Iron;

import java.util.Random;

public class IronOre extends Rock {
    public IronOre(double x, double y, double z, Random rand) {
        super(x, y, z, rand);
        inventory.addItemPile(new ItemPile(new Iron(), rand.nextInt(4)));
    }
}
