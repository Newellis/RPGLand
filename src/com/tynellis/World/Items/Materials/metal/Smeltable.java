package com.tynellis.World.Items.Materials.metal;

import com.tynellis.World.Items.Cookable;
import com.tynellis.World.Items.Item;

import java.util.Random;

public abstract class Smeltable extends Cookable {
    protected double oxide, sulfide;
    protected double oxidizing, sulfiding;
    protected double purity;

    public Smeltable(String name, int cookTemp, int cookTime, double oxidizing, double sulfiding, int maxStackSize, int artRow, int artCol, Random random) {
        super(name, cookTemp, cookTime, maxStackSize, artRow, artCol);
        this.oxidizing = oxidizing;
        this.sulfiding = sulfiding;
        do {
            if (random.nextDouble() < oxidizing) {
                oxide = random.nextDouble();
            }
            if (random.nextDouble() < sulfiding) {
                sulfide = random.nextDouble();
            }
        } while (oxide + sulfide > 1 || oxide + sulfide <= 0);
        purity = 1.0 - (sulfide + oxide);
        System.out.println("SetValues: " + oxide + " + " + sulfide + " Pure " + purity);
    }

    public Item CookTick(int temp, Random random, boolean oxygen) {
        if (cookTemp > temp && cookingTime < cookTime) {
            cookingTime++;
        } else {
            if (cookTime != cookingTime) {
                if (oxygen) {
                    double remaining = (20 + random.nextInt(300)) / 1000.0;
                    double oldSulfide = sulfide;
                    if (remaining < sulfide) {
                        sulfide = remaining + (((sulfide - remaining) / cookTime) * cookingTime);
                    }
                    if (sulfide != oldSulfide) {
                        if (random.nextDouble() < oxidizing) {
                            oxide += oldSulfide - sulfide;
                        }
                    }
                } else {
                    double remaining = (20 + random.nextInt(300)) / 1000.0;
                    if (remaining < oxide) {
                        oxide = remaining + (((oxide - remaining) / cookTime) * cookingTime);
                    }
                }
            }
        }
        purity = 1.0 - (sulfide + oxide);
        System.out.println("Purity: " + purity);
        return super.CookTick(temp, random);
    }
}
