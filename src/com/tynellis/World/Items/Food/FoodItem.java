package com.tynellis.World.Items.Food;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.animals.Animal;

public abstract class FoodItem implements Food {
    private FoodType type;
    private int nutrition;

    public FoodItem(FoodType type, int nutrition) {
        this.type = type;
        this.nutrition = nutrition;
    }

    @Override
    public FoodType getType() {
        return type;
    }

    @Override
    public boolean canEat(Animal.Diet e) {
        if (e == Animal.Diet.CARNIVORE && type == FoodType.MEAT) {
            return true;
        } else if (e == Animal.Diet.HERBIVORE && type == FoodType.PLANT) {
            return true;
        } else return e == Animal.Diet.OMNIVORE;
    }

    @Override
    public double getNutrition() {
        return nutrition;
    }

    @Override
    public void doneEating(Entity e) {
    }
}
