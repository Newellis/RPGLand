package com.tynellis.World.spawners;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.World;

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

    protected boolean validSpawnLocationFor(World world, Entity entity, int x, int y, int z) {
        return !(world.isTileObstructed(x, y, z) && !world.getTile(x, y, z).isPassableBy(entity)) && !exclusionArea.contains(new Point(x, y));
    }
}
