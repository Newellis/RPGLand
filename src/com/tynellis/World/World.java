package com.tynellis.World;

import com.tynellis.GameComponent;
import com.tynellis.Save.FileHandler;
import com.tynellis.World.Buildings.SmallHouse;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.ItemEntity;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Entities.NPC.villagers.LumberJackNpc;
import com.tynellis.World.Entities.Plants.Tree;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Entities.UsableEntity.Chest;
import com.tynellis.World.Entities.UsableEntity.Door;
import com.tynellis.World.Entities.UsableEntity.FirePit;
import com.tynellis.World.Entities.UsableEntity.RegionBoundary;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Items.Materials.Stone;
import com.tynellis.World.Items.Materials.metal.ores.Copper;
import com.tynellis.World.Items.Materials.metal.ores.Gold;
import com.tynellis.World.Items.Materials.metal.ores.Iron;
import com.tynellis.World.Items.Materials.metal.ores.Silver;
import com.tynellis.World.Items.Materials.metal.ores.Tin;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Land;
import com.tynellis.World.world_parts.Regions.CaveRegion;
import com.tynellis.World.world_parts.Regions.Generator.SurfaceGen;
import com.tynellis.World.world_parts.Regions.OverWorldRegion;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class World implements Land, Serializable {

    private Region overRegionRegion;
    private static Region currentRegion;
    private static final ArrayList<Region> loadedRegions = new ArrayList<Region>();

    private final long seed;
    private final Random WORLD_RAND;
    private String Name;
    public SurfaceGen gen;
    private int[] spawnPoint;
    private Region spawnRegion;

    public static final int Buffer = 2;
    public static final int X = 0, Y = 1;
    private transient int[] areaOffset;
    private transient int[] oldAreaOffset;
    private transient int NumOfAreasInWidth, NumOfAreasInHeight;

    private final Rectangle screenArea = new Rectangle();
    private Rectangle loadedArea = new Rectangle();

    public World(String name, long seed) {
        this.seed = seed;
        WORLD_RAND = new Random(seed);
        Name = name;
        FileHandler.setGameDir(Name);
        gen = new SurfaceGen(this);
        overRegionRegion = new OverWorldRegion("Surface", new SurfaceGen(this));
        overRegionRegion.addSpawnFreeArea(screenArea);
        currentRegion = overRegionRegion;
        updateLoadedRegions();
    }

    public void moveEntityToRegion(Entity entity, Region region) {
        synchronized (loadedRegions) {
            for (Region origin : loadedRegions) {
                if (!origin.equals(region)) {
                    origin.queueRemovalOfEntity(entity);
                }
            }
        }
        region.queueAdditionOfEntity(entity);
        if (GameComponent.isMainPlayer(entity)) {
            System.out.println("move to region " + region.getName());
            currentRegion = region;
        }
        updateLoadedRegions();
    }

    private void updateLoadedRegions() {
        if (!loadedRegions.contains(currentRegion)) {
            currentRegion.setAreaOffset(areaOffset);
            currentRegion.loadAreas(getLoadedAreaRect(), getRand(), seed);
        }
        ArrayList<Region> regions = new ArrayList<Region>();
        regions.add(currentRegion);
        ArrayList<Entity> entities = currentRegion.getEntitiesIntersecting(new Rectangle(loadedArea.x * Area.WIDTH * Tile.WIDTH, loadedArea.y * Area.HEIGHT * Tile.HEIGHT, loadedArea.width * Area.WIDTH * Tile.WIDTH, loadedArea.height * Area.HEIGHT * Tile.HEIGHT));
        for (Entity e : entities) {
            if (e instanceof RegionBoundary && !regions.contains(((RegionBoundary) e).getDestination())) {
                regions.add(((RegionBoundary) e).getDestination());
            }
        }
        for (Region region : regions) {
            if (!loadedRegions.contains(region)) {
                region.setAreaOffset(areaOffset);
                region.loadAreas(loadedArea, getRand(), seed);
            }
        }
        synchronized (loadedRegions) {
            for (Region region : loadedRegions) {
                if (!regions.contains(region)) {
                    region.saveLoadedAreas();
                }
            }
            loadedRegions.clear();
            loadedRegions.addAll(regions);
            System.out.println(regions);
        }
    }

    public void tick() {
        synchronized (loadedRegions) {
            for (int i = 0; i < loadedRegions.size(); i++) { //not foreach to avoid concurrent mod exception
                loadedRegions.get(i).tick(getRand());
            }
        }
    }

    public void render(Graphics g, int width, int height, int XPos, int YPos, int ZPos) {
        int xOffset = (width / 2) - XPos;
        int yOffset = ((height / 2) - YPos + ZPos);

        Rectangle screen = new Rectangle((XPos - (width / 2)), (YPos - (height / 2)), (width), (height));
        Rectangle renderArea = new Rectangle(screen.x - ((Area.WIDTH * Tile.WIDTH) / 2), screen.y - ((Area.HEIGHT * Tile.HEIGHT) / 2), screen.width + (Area.WIDTH * Tile.WIDTH), screen.height + (Area.HEIGHT * Tile.HEIGHT));
        screenArea.setBounds(screen.x / Tile.WIDTH, screen.y / Tile.HEIGHT, screen.width / Tile.WIDTH, screen.height / Tile.HEIGHT);

        //calculate how many areas across to just past the edge
        NumOfAreasInWidth = (width / (Tile.WIDTH * Area.WIDTH)) + (3 * Buffer);
        NumOfAreasInHeight = (height / (Tile.HEIGHT * Area.HEIGHT)) + (3 * Buffer);
        //store old and find area offset
        int newXOffset = (int) (screen.getX() / (Tile.WIDTH * Area.WIDTH)) - Buffer;
        int newYOffset = (int) (screen.getY() / (Tile.HEIGHT * Area.HEIGHT)) - Buffer;
        if (areaOffset[X] != newXOffset || areaOffset[Y] != newYOffset) {
            setAreaOffset(newXOffset, newYOffset);
        }
        loadedArea.setBounds(areaOffset[X], areaOffset[Y], NumOfAreasInWidth, NumOfAreasInHeight);

        currentRegion.render(g, xOffset, yOffset, renderArea);
    }

    public void setHalfNumOfAreas(int width, int height) {
        NumOfAreasInWidth = width;
        NumOfAreasInHeight = height;
    }

    //set centerArea and calculate areaOffset from it
    public void setAreaOffset(int x, int y) {
        if (areaOffset != null) {
            oldAreaOffset = new int[]{areaOffset[X], areaOffset[Y]};
        } else {
            oldAreaOffset = new int[]{x, y};
        }
        areaOffset = new int[]{x, y};
        loadedArea.setBounds(areaOffset[X], areaOffset[Y], NumOfAreasInWidth, NumOfAreasInHeight);
        updateLoadedRegions();
        synchronized (loadedRegions) {
            for (Region region : loadedRegions) {
                region.setAreaOffset(areaOffset);
                region.loadAreas(oldAreaOffset[X], oldAreaOffset[Y], loadedArea, getRand(), seed);
            }
        }
    }

    public void addEntity(Entity entity) {
        currentRegion.addEntity(entity);
    }

    public void removeEntity(Entity entity) {
        synchronized (loadedRegions) {
            for (Region region : loadedRegions) {
                region.removeEntity(entity);
            }
        }
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
    }

    public int[] getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Region region, int[] spawnPoint) {
        this.spawnPoint = spawnPoint;
        spawnRegion = region;

        setAreaOffset(areaOffset[X], areaOffset[Y]);//to set up old area offset
        loadedArea.setBounds(areaOffset[X], areaOffset[Y], NumOfAreasInWidth, NumOfAreasInHeight);
        loadAreas();
    }

    public void addPlayer(Player player) {
        currentRegion = spawnRegion;
        spawnRegion.addEntity(player);
    }

    public void saveLoadedAreas() {
        synchronized (loadedRegions) {
            for (Region region : loadedRegions) {
                region.saveLoadedAreas();
            }
        }
    }

    public void loadAreas() {
//        updateLoadedRegions();
        synchronized (loadedRegions) {
            for (Region region : loadedRegions) {
                region.loadAreas(oldAreaOffset[X], oldAreaOffset[Y], loadedArea, getRand(), seed);
            }
        }
    }

    public void addTestEntities() {
        //Testing entities
        Chest chest = new Chest(spawnPoint[0] - 6, spawnPoint[1] - 2, spawnPoint[2]);
        NpcBase npc = new LumberJackNpc(spawnPoint[0] - 3, spawnPoint[1] + 2, spawnPoint[2], NpcBase.NpcGender.FEMALE, chest, getRand());
        ItemEntity itemEntity = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 1), getRand(), spawnPoint[0] + 4, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity1 = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 3), getRand(), spawnPoint[0] + 6, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity2 = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 6), getRand(), spawnPoint[0] + 8, spawnPoint[1], spawnPoint[2]);
//        overRegionRegion.addEntity(npc);
        overRegionRegion.addEntity(itemEntity);
        overRegionRegion.addEntity(itemEntity1);
        overRegionRegion.addEntity(itemEntity2);
        overRegionRegion.addEntity(chest);
        SmallHouse.buildSmallHouse(overRegionRegion, getRand(), spawnPoint[0] - 6, spawnPoint[1] - 5, spawnPoint[2]);
        SmallHouse.buildSmallHouse(overRegionRegion, getRand(), spawnPoint[0], spawnPoint[1] - 5, spawnPoint[2]);

        overRegionRegion.addEntity(new FirePit(spawnPoint[0] + 4, spawnPoint[1] - 1, spawnPoint[2]));

        Region Caves = new CaveRegion(this, 1);
        Door CaveDoor = new Door(spawnPoint[0], spawnPoint[1] + 2, spawnPoint[2], 1, Caves);
        overRegionRegion.addEntity(CaveDoor);
        Caves.addEntity(new Door(spawnPoint[0], spawnPoint[1] + 3, spawnPoint[2], 1, overRegionRegion));
        ItemEntity itemEntity6 = new ItemEntity(new ItemPile(new Stone(), 10), getRand(), spawnPoint[0] + 4, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity7 = new ItemEntity(new ItemPile(new Copper(getRand()), 10), getRand(), spawnPoint[0] + 6, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity8 = new ItemEntity(new ItemPile(new Tin(getRand()), 10), getRand(), spawnPoint[0] + 8, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity3 = new ItemEntity(new ItemPile(new Gold(getRand()), 10), getRand(), spawnPoint[0] + 10, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity4 = new ItemEntity(new ItemPile(new Iron(getRand()), 10), getRand(), spawnPoint[0] + 12, spawnPoint[1], spawnPoint[2]);
        ItemEntity itemEntity5 = new ItemEntity(new ItemPile(new Silver(getRand()), 10), getRand(), spawnPoint[0] + 14, spawnPoint[1], spawnPoint[2]);
        Caves.addEntity(itemEntity6);
        Caves.addEntity(itemEntity7);
        Caves.addEntity(itemEntity8);
        Caves.addEntity(itemEntity3);
        Caves.addEntity(itemEntity4);
        Caves.addEntity(itemEntity5);
    }

    public Region getCurrentRegion() {
        return currentRegion;
    }

    public Region getRegionWithName(String name) {
        for (Region region : loadedRegions) {
            if (region.getName().equals(name)) {
                return region;
            }
        }
        return null;
    }
}
