package com.tynellis.World.Items.Food;

import com.tynellis.World.Entities.Entity;

public interface Food {
    enum FoodType {
        PLANT,
        MEAT,
        COMBO,
        ;
    }

    FoodType getType();

    boolean canEat(Entity e);

    int getNutrition();

    void doneEating(Entity e);
}
