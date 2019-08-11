package com.tynellis.World.Entities.Plants.Crops;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Plants.Plant;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.List;
import java.util.Random;

public abstract class Crop extends Plant {

    private int GrowDelay;
    private int MaxGrowDelay = 2 * 60 * 20; // mins * secs * tick per sec
    private boolean fullyGrown = false;
    private int growthStage;
    private int maxGrowthStage;

    public Crop(double x, double y, double z, Random random, int maxGrowth, int currentGrowth) {
        super(x, y, z, Tile.WIDTH, Tile.HEIGHT);
        GrowDelay = random.nextInt(MaxGrowDelay);
        maxGrowthStage = maxGrowth;
        growthStage = currentGrowth;
    }

    public void tick(Region region, Random random, List<Entity> near) {
        if (!fullyGrown) {
            GrowDelay--;
            if (GrowDelay <= 0) {
                Grow(random);
                GrowDelay = random.nextInt(MaxGrowDelay);
            }
        }
        super.tick(region, random, near);
    }

    public void increaseGrowthStage() {
        growthStage++;
        if (growthStage >= maxGrowthStage) {
            fullyGrown = true;
        }
    }

    protected abstract void Grow(Random random);
}
