package com.tynellis.World.spawners;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.world_parts.Region;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

public class MinMaxRangeSpawner extends Spawner {
    private Rectangle exclusionArea;

    public MinMaxRangeSpawner(int speed, Rectangle maxArea, Rectangle minArea, Map<Class, Integer> spawnables) {
        super(speed, maxArea, spawnables);
        exclusionArea = minArea;
    }

    public MinMaxRangeSpawner(int speed, Rectangle maxArea, Rectangle minArea) {
        super(speed, maxArea);
    }

    protected boolean validSpawnLocationFor(Region region, Entity entity, int x, int y, int z) {
        return !(region.isTileObstructed(x, y, z) && !region.getTile(x, y, z).isPassableBy(entity)) && !exclusionArea.contains(new Point(x, y));
    }
}
