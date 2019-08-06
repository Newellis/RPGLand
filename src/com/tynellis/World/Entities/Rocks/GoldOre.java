package com.tynellis.World.Entities.Rocks;

import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.metal.Nuggets.GoldNug;
import com.tynellis.World.Items.Materials.metal.ores.Gold;

import java.util.Random;

public class GoldOre extends Rock {
    public GoldOre(double x, double y, double z, Random rand) {
        super(x, y, z, rand);
        inventory.addItemPile(new ItemPile(new Gold(rand), rand.nextInt(2)));
        inventory.addItemPile(new ItemPile(new GoldNug(0.8 + (rand.nextInt(200) / 1000.0)), rand.nextInt(5)));
    }
}
