package com.tynellis.World.Entities.Rocks;

import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.ores.Silver;

import java.util.Random;

public class SilverOre extends Rock {
    public SilverOre(double x, double y, double z, Random rand) {
        super(x, y, z, rand);
        inventory.addItemPile(new ItemPile(new Silver(), rand.nextInt(4)));
    }
}
