package com.tynellis.World.Buildings;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.ItemEntity;
import com.tynellis.World.Entities.Plants.Plant;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

public abstract class Building extends Entity implements BoundingBoxOwner {
    public Building(double x, double y, double z, int width, int height) {
        super(x, y, z, width, height);
        speed = 0.0;
        canBeMoved = false;
    }

    @Override
    public void tick(Region region, Random random, List<Entity> near) {
        for (Entity e : region.getEntitiesIntersecting(getBounds())) {
            if (e instanceof Plant || e instanceof ItemEntity) {
                e.kill();
            }
        }
        super.tick(region, random, near);
    }

    @Override
    public void performDeath(Region region, Random random) {
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
