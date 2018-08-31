package com.tynellis.World.Buildings;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.World;

import java.awt.Graphics;

public abstract class Building extends Entity implements BoundingBoxOwner {
    public Building(double x, double y, double z, int width, int height) {
        super(x, y, z, width, height);
        canBeMoved = false;
    }

    public abstract void render(Graphics g, int xOffset, int yOffset);

    @Override
    public void performDeath(World world) {
        //use if want to ever add ability to destroy buildings
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return false;
    }

    @Override
    public boolean isPassableBy(Entity.movementTypes movementType) {
        return false;
    }
}
