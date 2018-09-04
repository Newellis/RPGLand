package com.tynellis.World.spawners;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Spawner implements Serializable {
    private int spawnTime, spawnCoolDown;
    private Map<Class, Integer> creatures;
    protected Rectangle spawnArea;

    public Spawner(int speed, Rectangle area, Map<Class, Integer> spawnables) {
        spawnTime = speed;
        creatures = spawnables;
        spawnArea = area;
    }

    public Spawner(int speed, Rectangle area, Class entity, int amount) {
        spawnTime = speed;
        creatures = new HashMap<Class, Integer>();
        creatures.put(entity, amount);
        spawnArea = area;
    }

    public Spawner(int speed, Rectangle area) {
        spawnTime = speed;
        spawnArea = area;
        creatures = new HashMap<Class, Integer>();
    }

    public void tick(Region region, Random random) {
        if (spawnCoolDown <= 0 && creatures.size() > 0) {

            boolean sucessful = false;
            Object[] keys = creatures.keySet().toArray();
            ArrayList<Class> attempted = new ArrayList<Class>();
            do {
                Class<?> type;
                do {
                    type = (Class) keys[random.nextInt(keys.length)];
                    if (attempted.size() >= creatures.size()) {
                        spawnCoolDown = spawnTime;
                        return;
                    }
                } while (attempted.contains(type));
                attempted.add(type);
                ArrayList<Entity> inArea = region.getEntitiesIntersecting(spawnArea);
                int typeInArea = 0;
                for (Entity e : inArea) {
                    if (type.isInstance(e)) {
                        typeInArea++;
                    }
                }
                if (typeInArea >= creatures.get(type)) {
                    continue;
                }
                try {
                    Entity entity = (Entity) type.getDeclaredConstructor(new Class[]{int.class, int.class, int.class, Random.class}).newInstance(spawnArea.x / Tile.WIDTH, spawnArea.y / Tile.HEIGHT, region.getTopLayerAt(spawnArea.x / Tile.WIDTH, spawnArea.y / Tile.HEIGHT), random);
                    int x, y, z, attempts = 0;
                    do {
                        x = (spawnArea.x / Tile.WIDTH) + (random.nextInt(spawnArea.width / Tile.WIDTH));
                        y = (spawnArea.y / Tile.HEIGHT) + (random.nextInt(spawnArea.height / Tile.HEIGHT));
                        z = region.getTopLayerAt(x, y);
                        attempts++;
                    }
                    while (attempts <= 10 && !validSpawnLocationFor(region, entity, x, y, z));
                    if (attempts <= 10) {
                        entity.setLocation(x, y, z);
                        region.queueAdditionOfEntity(entity);
                        sucessful = true;
                    }

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } while (!sucessful);
            spawnCoolDown = spawnTime;
        } else {
            spawnCoolDown--;
        }
    }

    protected boolean validSpawnLocationFor(Region region, Entity entity, int x, int y, int z) {
        return !(region.isTileObstructed(x, y, z) && !region.getTile(x, y, z).isPassableBy(entity));
    }

    public boolean addEntitySpawn(Class spawn, int maxAmount) {
        if (!creatures.containsKey(spawn)) {
            creatures.put(spawn, maxAmount);
            return true;
        }
        return false;
    }
}
