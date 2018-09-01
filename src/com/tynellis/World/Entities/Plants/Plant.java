package com.tynellis.World.Entities.Plants;

import com.tynellis.World.Entities.KillableEntity;

public abstract class Plant extends KillableEntity {
    public Plant(double x, double y, double z, int width, int height) {
        super(x, y, z, width, height);
        speed = 0.0;
        canBeMoved = false;
    }
}
