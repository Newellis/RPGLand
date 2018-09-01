package com.tynellis.World.Buildings;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Rectangle;
import java.util.Random;

public abstract class Building extends Entity implements BoundingBoxOwner {
    public Building(double x, double y, double z, int width, int height) {
        super(x, y, z, width, height);
        speed = 0.0;
        canBeMoved = false;
    }

    public static Building makeRandomHouse(int x, int y, int z, Random random) {
        int width = random.nextInt(7) + 3;
        if (random.nextBoolean() && width > 6) {
            width /= 2;
        }
        int height = (int) Math.ceil(width / 2.0) + random.nextInt(width);
        return new SmallHouse(x, y, z, width, height, random);
    }

    @Override
    public void performDeath(World world) {
        //use if want to ever add ability to destroy buildings
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return false;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) ((posX + 0.5) * Tile.WIDTH) - (width / 2), (int) ((posY + 0.5) * Tile.HEIGHT) - height - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), width, height);
    }

    @Override
    public boolean isPassableBy(Entity.movementTypes movementType) {
        return false;
    }
}
