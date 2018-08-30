package com.tynellis.World.spawners;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

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
    private Rectangle spawnArea;

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

    public void tick(World world) {
        if (spawnCoolDown <= 0) {

            boolean sucessful = false;
            Object[] keys = creatures.keySet().toArray();
            ArrayList<Class> attempted = new ArrayList<Class>();
            do {
                Class<?> type;
                do {
                    type = (Class) keys[world.getRand().nextInt(keys.length)];
                    if (attempted.size() >= creatures.size()) {
                        spawnCoolDown = spawnTime;
                        return;
                    }
                } while (attempted.contains(type));
                attempted.add(type);
                ArrayList<Entity> inArea = world.getEntitiesIntersecting(spawnArea);
                int typeInArea = 0;
                for (Entity e : inArea) {
                    if (type.isInstance(e)) {
                        typeInArea++;
                    }
                }
                System.out.println("found " + typeInArea + " of " + type.getSimpleName() + " in area");
                if (typeInArea >= creatures.get(type)) {
                    continue;
                }
                System.out.println("Spawn new: " + type.getSimpleName());

                try {
                    Entity entity = (Entity) type.getDeclaredConstructor(new Class[]{int.class, int.class, int.class, Random.class}).newInstance(spawnArea.x / Tile.WIDTH, spawnArea.y / Tile.HEIGHT, world.getTopLayerAt(spawnArea.x / Tile.WIDTH, spawnArea.y / Tile.HEIGHT), world.getRand());
                    int x, y, z, attempts = 0;
                    do {
                        x = (spawnArea.x / Tile.WIDTH) + (world.getRand().nextInt(spawnArea.width / Tile.WIDTH));
                        y = (spawnArea.y / Tile.HEIGHT) + (world.getRand().nextInt(spawnArea.height / Tile.HEIGHT));
                        z = world.getTopLayerAt(x, y);
                        attempts++;
                    }
                    while (attempts <= 10 && world.isTileObstructed(x, y, z) && !world.getTile(x, y, z).isPassableBy(entity));
                    if (attempts <= 10) {
                        entity.setLocation(x, y, z);
                        world.queueAdditionOfEntity(entity);
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

//    Constructor<?> ctor = clazz.getConstructor(String.class);
//    Object object = ctor.newInstance(new Object[] { ctorArgument });
}