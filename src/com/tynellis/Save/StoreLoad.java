package com.tynellis.Save;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.World;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Region;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

public class StoreLoad {
    public static void StoreRemovedArea(Area area, Region region, int X, int Y) {
        Rectangle bounds = region.getAreaBounds(X, Y);
        ArrayList<Entity> entities = region.getEntitiesInBounds(bounds);
        for (Entity e: entities){
            region.removeEntity(e);
        }
        SavedArea save = new SavedArea(area, entities);
        File file = new File(FileHandler.getRegionDir(region), "x" + X + "y" + Y + FileHandler.extension);
        FileHandler.store(save, file);
    }

    public static void StoreArea(Area area, Region region, int X, int Y) {
        Rectangle bounds = region.getAreaBounds(X, Y);
        ArrayList<Entity> entities = region.getEntitiesInBounds(bounds);
        SavedArea save = new SavedArea(area, entities);
        File file = new File(FileHandler.getRegionDir(region), "x" + X + "y" + Y + FileHandler.extension);
        FileHandler.store(save, file);
    }

    public static SavedArea LoadArea(Region region, int X, int Y) {
        File file = new File(FileHandler.getRegionDir(region), "x" + X + "y" + Y + FileHandler.extension);
        Object o = FileHandler.load(file);
        if (o == null) return null;
        return (SavedArea)o;
    }
    public static void StorePlayer(Player player) {
        File file = new File(FileHandler.getCharDir(), player.getName() + FileHandler.extension);
        FileHandler.store(player, file);
    }
    public static Player LoadPlayer(String name) {
        File file = new File(FileHandler.getCharDir(), name + FileHandler.extension);
        Object o = FileHandler.load(file);
        if (o == null) return null;
        return (Player)o;
    }

    public static void StoreWorld(World world, String playerName) {
        File file = new File(FileHandler.getGameDir(), "world" + FileHandler.extension);
        SavedWorld save = new SavedWorld(world, playerName);
        FileHandler.store(save, file);
    }

    public static SavedWorld LoadWorld() {
        File file = new File(FileHandler.getGameDir(),"world" + FileHandler.extension);
        Object o = FileHandler.load(file);
        if (o == null) return null;
        return (SavedWorld)o;
    }
}
