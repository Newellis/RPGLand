package com.tynellis.Save;

import com.tynellis.Entities.Entity;
import com.tynellis.Entities.Player;
import com.tynellis.World.Area;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class StoreLoad {
    public static void StoreRemovedArea(Area area, World world, int X, int Y) {
        Rectangle bounds = world.getAreaBounds(X, Y);
        ArrayList<Entity> entities = world.getEntitiesInBounds(bounds);
        for (Entity e: entities){
            world.removeEntity(e);
        }
        SavedArea save = new SavedArea(area, entities);
        File file = new File(FileHandler.getGameDir(),"x" + X + "y" + Y + FileHandler.extension);
        FileHandler.store(save, file);
    }
    public static void StoreArea(Area area, World world, int X, int Y){
        Rectangle bounds = world.getAreaBounds(X, Y);
        ArrayList<Entity> entities = world.getEntitiesInBounds(bounds);
        SavedArea save = new SavedArea(area, entities);
        File file = new File(FileHandler.getGameDir(),"x" + X + "y" + Y + FileHandler.extension);
        FileHandler.store(save, file);
    }
    public static SavedArea LoadArea(int X, int Y) {
        File file = new File(FileHandler.getGameDir(),"x" + X + "y" + Y + FileHandler.extension);
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
        File file = new File(FileHandler.getGameDir(),"world" + FileHandler.extension);
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
