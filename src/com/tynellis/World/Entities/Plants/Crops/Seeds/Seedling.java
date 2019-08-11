package com.tynellis.World.Entities.Plants.Crops.Seeds;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Plants.Crops.Crop;

import java.util.Random;

public abstract class Seedling extends Crop {

    public Seedling(double x, double y, double z, Random random, int maxGrowth, int currentGrowth) {
        super(x, y, z, random, maxGrowth, currentGrowth);
    }

    public Seedling(double x, double y, double z, Random random, int maxGrowth) {
        this(x, y, z, random, maxGrowth, 0);
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return true;
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return true;
    }
}
