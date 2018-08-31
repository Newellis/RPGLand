package com.tynellis.World.spawners;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.World;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Map;

public class WorldSpawner extends Spawner {
    private ArrayList<Rectangle> exclusionAreas;

    public WorldSpawner(int speed, Map<Class, Integer> spawnables) {
        super(speed, new Rectangle(), spawnables);
    }

    public WorldSpawner(int speed) {
        super(speed, new Rectangle());
    }

    public void tick(World world) {
        spawnArea = world.getLoadedAreaBounds();
        exclusionAreas = world.getSpawnFreeAreas();
        super.tick(world);
    }

    protected boolean validSpawnLocationFor(World world, Entity entity, int x, int y, int z) {
        for (Rectangle area : exclusionAreas) {
            System.out.println(area + " contains " + x + ", " + y);
            if (area.contains(new Point(x, y))) {
                System.out.println("Block Spawn at " + x + ", " + y);
                return false;
            }
        }
        return !(world.isTileObstructed(x, y, z) && (world.getTile(x, y, z) != null && !world.getTile(x, y, z).isPassableBy(entity)));
    }
}
