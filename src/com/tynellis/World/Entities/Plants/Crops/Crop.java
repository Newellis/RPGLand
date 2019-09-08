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
    protected int growthStage;
    protected int maxGrowthStage;
    private String name;

    public Crop(String name, double x, double y, double z, Random random, int maxGrowth, int currentGrowth) {
        this(name, x, y, z, random, maxGrowth, currentGrowth, Tile.WIDTH, Tile.HEIGHT);
    }

    public Crop(String name, double x, double y, double z, Random random, int maxGrowth, int currentGrowth, int width, int height) {
        super(x, y, z, width, height);
        this.name = name;
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

    public abstract Crop newCrop(Random rand, double x, double y, double z);

    public String getName() {
        return name;
    }
}
