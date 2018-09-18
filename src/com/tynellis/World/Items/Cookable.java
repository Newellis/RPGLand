package com.tynellis.World.Items;

import java.util.Random;

public abstract class Cookable extends Item {

    protected int cookTemp;
    protected final int cookTime;
    protected int cookingTime;

    public Cookable(String name, int cookTemp, int cookTime, int maxStackSize, int artRow, int artCol) {
        super(name, maxStackSize, artRow, artCol);
        this.cookTemp = cookTemp;
        cookingTime = cookTime;
        this.cookTime = cookTime;
    }

    public Item CookTick(int temp, Random random) {
        if (cookTemp <= temp) {
            cookingTime -= random.nextInt(3 + (temp - cookTemp));
            System.out.println("Cook " + getName() + ": " + cookingTime + " at " + temp);
            if (cookingTime <= 0) {
                return getCooked();
            }
        }
        return null;
    }

    protected abstract Item getCooked();
}
