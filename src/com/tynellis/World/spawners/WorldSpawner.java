package com.tynellis.World.spawners;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.world_parts.Region;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class WorldSpawner extends Spawner {
    private ArrayList<Rectangle> exclusionAreas;

    public WorldSpawner(int speed, Map<Class, Integer> spawnables) {
        super(speed, new Rectangle(), spawnables);
    }

    public WorldSpawner(int speed) {
        super(speed, new Rectangle());
    }

    public void tick(Region region, Random random) {
        spawnArea = region.getLoadedAreaBounds();
        exclusionAreas = region.getSpawnFreeAreas();
        super.tick(region, random);
    }

    protected boolean validSpawnLocationFor(Region region, Entity entity, int x, int y, int z) {
        for (Rectangle area : exclusionAreas) {
            if (area.contains(new Point(x, y))) {
                return false;
            }
        }
        return !(region.isTileObstructed(x, y, z) && (region.getTile(x, y, z) != null && !region.getTile(x, y, z).isPassableBy(entity)));
    }
}
