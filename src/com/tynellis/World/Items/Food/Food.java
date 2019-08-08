package com.tynellis.World.Items.Food;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.animals.Animal;

public interface Food {
    enum FoodType {
        PLANT,
        MEAT,
        COMBO,
        ;
    }

    FoodType getType();

    boolean canEat(Animal.Diet e);

    double getNutrition();

    void doneEating(Entity e);
}
