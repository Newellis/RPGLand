package com.tynellis.World.Items.Food;

import com.tynellis.World.Entities.Entity;

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
    public boolean canEat(Entity e) {
        return true;
    }

    @Override
    public int getNutrition() {
        return nutrition;
    }

    @Override
    public void doneEating(Entity e) {
    }
}
