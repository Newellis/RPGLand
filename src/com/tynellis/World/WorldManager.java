package com.tynellis.World;

import com.tynellis.Save.FileHandler;
import com.tynellis.World.Buildings.SmallHouse;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.ItemEntity;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Entities.NPC.villagers.LumberJackNpc;
import com.tynellis.World.Entities.Plants.Tree;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Entities.UsableEntity.Chest;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Land;
import com.tynellis.World.world_parts.Region;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class WorldManager implements Land, Serializable {

    private Region overRegionRegion;
    private Region currentRegion;
    private ArrayList<Region> loadedRegions;

    private final long seed;
    private final Random WORLD_RAND;
    private String Name;
    public WorldGen gen;
    private static int[] spawnPoint;
    private Region spawnRegion;

    public static final int Buffer = 2;
    public static final int X = 0, Y = 1;
    private transient int[] areaOffset;
    private transient int[] oldAreaOffset;
    private transient int NumOfAreasInWidth, NumOfAreasInHeight;

    private final Rectangle screenArea = new Rectangle();
    private Rectangle loadedArea = new Rectangle();

    public WorldManager(String name, long seed) {
        this.seed = seed;
        WORLD_RAND = new Random(seed);
        Name = name;
        FileHandler.setGameDir(Name);
        gen = new WorldGen(this);
        overRegionRegion = new Region(name, seed);
        currentRegion = overRegionRegion;
        loadedRegions = new ArrayList<Region>();
        loadedRegions.add(overRegionRegion);
    }

    public void tick() {
        overRegionRegion.setAreaOffset(areaOffset);
        //load areas if old and new area offset are different
        loadAreas();

        overRegionRegion.tick(getRand());
    }

    public void render(Graphics g, int width, int height, int XPos, int YPos, int ZPos) {
        int xOffset = (width / 2) - XPos;
        int yOffset = ((height / 2) - YPos + ZPos);

//        screenArea.setBounds(((XPos - (width / 2)) / Tile.WIDTH) - 2, ((YPos - (height / 2)) / Tile.WIDTH) - 2, (width / Tile.WIDTH) + 4, (height / Tile.WIDTH) + 4);
        screenArea.setBounds((XPos - (width / 2)), (YPos - (height / 2)), (width), (height));

        //calculate how many areas across to just past the edge
        NumOfAreasInWidth = (width / (Tile.WIDTH * Area.WIDTH)) + (3 * Buffer);
        NumOfAreasInHeight = (height / (Tile.HEIGHT * Area.HEIGHT)) + (3 * Buffer);
        //store old and find area offset
//        setAreaOffset((XPos / (Tile.WIDTH * Area.WIDTH) - (NumOfAreasInWidth / 2)) - Buffer, (YPos / (Tile.HEIGHT * Area.HEIGHT) - (NumOfAreasInHeight / 2)) - Buffer);
//        setAreaOffset((XPos / (Tile.WIDTH * Area.WIDTH)) - Buffer, (YPos / (Tile.HEIGHT * Area.HEIGHT)) - Buffer);
        setAreaOffset((int) (screenArea.getX() / (Tile.WIDTH * Area.WIDTH)) - Buffer, (int) (screenArea.getY() / (Tile.HEIGHT * Area.HEIGHT)) - Buffer);
        loadedArea.setBounds(areaOffset[X], areaOffset[Y], NumOfAreasInWidth, NumOfAreasInHeight);

        currentRegion.render(g, xOffset, yOffset, screenArea);

        g.setColor(Color.BLUE);
        g.drawRect(screenArea.x + xOffset, screenArea.y + yOffset, screenArea.width, screenArea.height);

    }

    public void setHalfNumOfAreas(int width, int height) {
        NumOfAreasInWidth = width;
        NumOfAreasInHeight = height;
    }

    //set centerArea and calculate areaOffset from it
    public void setAreaOffset(int x, int y) {
        if (areaOffset != null) oldAreaOffset = new int[]{areaOffset[X], areaOffset[Y]};
        areaOffset = new int[]{x, y};
        for (Region region : loadedRegions) {
            region.setAreaOffset(areaOffset);
        }
        loadedArea.setBounds(areaOffset[X], areaOffset[Y], NumOfAreasInWidth, NumOfAreasInHeight);
    }

    public void addEntity(Region region, Entity entity) {
        region.addEntity(entity);
    }

    public void addEntity(Entity entity) {
        currentRegion.addEntity(entity);
    }

    public void removeEntity(Region region, Entity entity) {
        region.removeEntity(entity);
    }

    public void removeEntity(Entity entity) {
        currentRegion.removeEntity(entity);
    }

    public Tile getTile(Region region, int X, int Y, int Z) {
        return region.getTile(X, Y, Z);
    }

    public void setTile(Region region, Tile tile, int X, int Y, int Z) {
        region.setTile(tile, X, Y, Z);
    }

    @Override
    public Tile getTile(int X, int Y, int Z) {
        return currentRegion.getTile(X, Y, Z);
    }

    @Override
    public void setTile(Tile tile, int X, int Y, int Z) {
        currentRegion.setTile(tile, X, Y, Z);
    }

    public String getName() {
        return Name;
    }

    public Random getRand() {
        return WORLD_RAND;
    }

    public Rectangle getLoadedAreaRect() {
        return loadedArea;
    }

    public void genSpawn(long seed) {
        gen.setSpawn(overRegionRegion, seed);
//        setSpawnPoint(overRegionRegion, new int[]{1000, 4000, 0});
    }

    public static int[] getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Region region, int[] spawnPoint) {
        this.spawnPoint = spawnPoint;
        spawnRegion = region;

        setAreaOffset(spawnPoint[X] - 3, spawnPoint[Y] - 4);
        loadedArea.setBounds(areaOffset[X], areaOffset[Y], NumOfAreasInWidth, NumOfAreasInHeight);
        loadAreas();
    }

    public void addPlayer(Player player) {
        currentRegion = spawnRegion;
        spawnRegion.addEntity(player);
    }

    public void saveLoadedAreas() {
        for (Region region : loadedRegions) {
            region.saveLoadedAreas();
        }
    }

    public void loadAreas() {
        for (Region region : loadedRegions) {
            region.loadAreas(loadedArea, gen, getRand(), seed);
        }
    }

    public void addTestEntities() {
        //Testing entities
        Chest chest = new Chest(spawnPoint[0] - 10, spawnPoint[1] - 5, spawnPoint[2]);
        NpcBase npc = new LumberJackNpc(spawnPoint[0] - 3, spawnPoint[1] + 2, spawnPoint[2], NpcBase.NpcGender.FEMALE, chest, getRand());
        ItemEntity itemEntity = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 1), getRand(), spawnPoint[0] + 4, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity1 = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 10), getRand(), spawnPoint[0] + 6, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity2 = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 20), getRand(), spawnPoint[0] + 8, spawnPoint[1], spawnPoint[2]);
        overRegionRegion.addEntity(npc);
        overRegionRegion.addEntity(itemEntity);
        overRegionRegion.addEntity(itemEntity1);
        overRegionRegion.addEntity(itemEntity2);
        overRegionRegion.addEntity(chest);
        SmallHouse.buildSmallHouse(overRegionRegion, getRand(), spawnPoint[0] - 6, spawnPoint[1] - 5, spawnPoint[2]);
        SmallHouse.buildSmallHouse(overRegionRegion, getRand(), spawnPoint[0], spawnPoint[1] - 5, spawnPoint[2]);
    }

}
