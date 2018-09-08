package com.tynellis.World.Items;

import java.util.Random;

public abstract class Smeltable extends Cookable {
    public Smeltable(String name, int cookTemp, int cookTime, int maxStackSize, int artRow, int artCol) {
        super(name, cookTemp, cookTime, maxStackSize, artRow, artCol);
    }

    public Item CookTick(int temp, Random random) {
        if (cookTemp > temp && cookingTime < cookTime) {
            cookingTime++;
        }
        return super.CookTick(temp, random);
    }
}
