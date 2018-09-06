package com.tynellis.World.Entities.Rocks;

import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.ores.Gold;

import java.util.Random;

public class GoldOre extends Rock {
    public GoldOre(double x, double y, double z, Random rand) {
        super(x, y, z, rand);
        inventory.addItemPile(new ItemPile(new Gold(), rand.nextInt(4)));
    }
}
