package com.tynellis.World.Entities.Living.villagers;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Items.ItemPile;

import java.util.Random;

public class FarmerNpc extends Villager {
    public FarmerNpc(int x, int y, int z, Gender gender, Random random) {
        super(x, y, z, gender, random);
        canPickUpItems = true;
    }

    @Override
    public ItemPile[] getItemsToDrop(Random rand) {
        return new ItemPile[0];
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }
}
