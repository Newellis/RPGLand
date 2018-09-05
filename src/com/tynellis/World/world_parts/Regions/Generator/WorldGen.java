package com.tynellis.World.world_parts.Regions.Generator;

import com.tynellis.GameComponent;
import com.tynellis.World.Entities.Plants.Tree;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.LandTiles.ManMade.Ladder;
import com.tynellis.World.Tiles.LandTiles.Natural.Grass;
import com.tynellis.World.Tiles.LandTiles.Natural.Sand;
import com.tynellis.World.Tiles.LandTiles.Natural.Snow;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.Tiles.Water;
import com.tynellis.World.World;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Random;

public class WorldGen implements IWorldGen, Serializable {
    private World world;
    public static final int SEA_LEVEL = 25;
    public static final int BEACH_MAX_LEVEL = 50;
    public static final int HILL_LEVEL = 300;//150;
    public static final int MOUNTAIN_BASE_LEVEL = 500;//235;
    public static final int SLOPE_LEVEL = 600;//300;
    public static final int TREE_LEVEL = 700;//350;
    public static final int SNOW_LEVEL = 800;//400;

    private int[][] landAreas;

    public WorldGen(World world) {
        this.world = world;
        genLand();
    }

    public void setSpawn(Region region, long seed) {
        Random rand = new Random(seed);
        boolean viableArea = false, viable = false;
        while (!viableArea) {
            int x = rand.nextInt(Region.WIDTH),
                    y = rand.nextInt(Region.HEIGHT);
            if (landAreas[x][y] > SEA_LEVEL && landAreas[x][y] < TREE_LEVEL) {
                world.setHalfNumOfAreas((GameComponent.GAME_WIDTH / (Tile.WIDTH * Area.WIDTH)) + (3 * World.Buffer), (GameComponent.GAME_HEIGHT / (Tile.HEIGHT * Area.HEIGHT)) + (3 * World.Buffer));
                world.setAreaOffset(x - 3, y - 4);
                region.loadAreas(world.getLoadedAreaRect(), world.getRand(), seed);
                while (!viable) {
                    int areaX = rand.nextInt(2 * Area.WIDTH) - Area.WIDTH,
                            areaY = rand.nextInt(2 * Area.HEIGHT) - Area.HEIGHT;
                    Tile tile = region.getTile((x * Area.WIDTH) + areaX, (y * Area.HEIGHT) + areaY, region.getTopLayerAt((x * Area.WIDTH) + areaX, (y * Area.HEIGHT) + areaY));
                    Rectangle tileBounds = tile.getBounds();
                    tileBounds.setLocation((x * Area.WIDTH) + areaX, (y * Area.HEIGHT) + areaY);
                    if (tile instanceof LandTile && region.getEntitiesInBounds(tileBounds).size() <= 0) {
                        world.setSpawnPoint(region, new int[]{(x * Area.WIDTH) + areaX, (y * Area.HEIGHT) + areaY, region.getTopLayerAt((x * Area.WIDTH) + areaX, (y * Area.HEIGHT) + areaY)});
                        viable = true;
                    }
                }
                viableArea = true;
            }
        }
    }

    public void genLand() {
        int pow = 5;
        int[][] seedArea = seedArea(pow);
        landAreas = diamondStep(seedArea, Region.WIDTH, 2 * ((Region.WIDTH * Area.WIDTH) / (int) Math.pow(2, pow)), world.getRand(), true);//25
    }

    private int[][] seedArea(int powerOf2){
        int seedWidthNum = (int)Math.pow(2,powerOf2) + 1;
        int[][] seedArea = new int[seedWidthNum][seedWidthNum];
        for (int x = 0; x < seedWidthNum; x++) {
            for (int y = 0; y < seedWidthNum; y++) {
                if (x == 0 || x == seedWidthNum-1){
                    seedArea[x][y] = 0;
                } else if (y == 0 || y == seedWidthNum-1){
                    seedArea[x][y] = 0;
                } else if (world.getRand().nextBoolean()) {
                    seedArea[x][y] = world.getRand().nextInt(50);
                }
                 else {
                    seedArea[x][y] = world.getRand().nextInt(50);
                }

            }
        }
        return seedArea;
    }

    private int[][] diamondStep(int[][] area, int maxSize, int rand, Random random, boolean flip){
        if (area.length >= maxSize){
            return area;
        } else {
            int[][] newArea = new int[2 * area.length - 1][2 * area[0].length - 1];
            for (int x = 1; x < newArea.length; x += 2) {
                for (int y = 1; y < newArea[x].length; y += 2) {
                    if (x + 1 < newArea.length && y + 1 < newArea[x].length) { //copy cords to new array
                        newArea[x - 1][y - 1] = area[(x - 1) / 2][(y - 1) / 2];
                        newArea[x - 1][y + 1] = area[(x - 1) / 2][(y + 1) / 2];
                        newArea[x + 1][y - 1] = area[(x + 1) / 2][(y - 1) / 2];
                        newArea[x + 1][y + 1] = area[(x + 1) / 2][(y + 1) / 2];
                    }
                    int offset = rand;
                    if (rand > 0) {
                        if (flip) {
                            offset = random.nextInt(2 * rand) - rand + 1;
                        } else {
                            offset = random.nextInt(rand);
                        }
                    }
                    newArea[x][y] = offset + (int) Math.round((newArea[x - 1][y - 1] + newArea[x - 1][y + 1] + newArea[x + 1][y - 1] + newArea[x + 1][y + 1]) / 4.0);
                }
            }
            area = newArea;
        }
        int newRand = rand/2;
        return squareStep(area, maxSize, newRand, random, flip);
    }

    private int[][] squareStep(int[][] area, int maxSize, int rand, Random random, boolean flip) {
        for (int x = 0; x < area.length; x++) {
            for (int y = 0; y < area[x].length; y++) {
                if (area[x][y] == 0) {
                    int s1 = 0,s2 = 0,s3 = 0,s4 = 0,count = 0;
                    if (x>0){
                        s1 = area[x-1][y];
                        count++;
                    } else
                    if (y>0){
                        s2 = area[x][y-1];
                        count++;
                    }
                    if (x<area.length-1){
                        s3 = area[x+1][y];
                        count++;
                    }
                    if (y<area[x].length-1){
                        s4 = area[x][y+1];
                        count++;
                    }
                    int offset = rand;
                    if (rand > 0){
                        if (flip) {
                            offset = random.nextInt(2 * rand) - rand + 1;
                        } else {
                            offset = random.nextInt(rand) + 1;
                        }
                    }
                    area[x][y] = offset + ((s1+s2+s3+s4)/count);
                }
            }
        }
        int newRand = rand/2;
        return diamondStep(area, maxSize, newRand, random, flip);
    }

    public int[][] erodeArea(int X, int Y, Random rand) {
        int[][] land = new int[4][4];//{{landAreas[X/ Area.WIDTH][Y/ Area.WIDTH], landAreas[(X/Area.WIDTH) +1][Y/ Area.WIDTH]},{landAreas[X/ Area.WIDTH][(Y/ Area.WIDTH) + 1], landAreas[(X/ Area.WIDTH) +1][(Y/ Area.WIDTH) + 1]}};
        for (int x = 0; x < land.length; x++){
            for(int y = 0; y < land[x].length; y++){
                if (X/ Area.WIDTH + x - 1 >= 0 && Y/ Area.HEIGHT + y - 1 >= 0 && X/ Area.WIDTH + x - 1 < landAreas.length && Y/ Area.HEIGHT + y - 1 < landAreas[x].length){
                    land[x][y] = landAreas[X/ Area.WIDTH + x - 1][Y/ Area.HEIGHT + y - 1];
                } else {
                    land[x][y] = rand.nextInt(25);
                }
            }
        }
        land = diamondStep(land, 3 * Area.WIDTH, (Area.WIDTH/(int)Math.pow(2,2)), rand, false);
        return land;
    }

    public void fillArea(Region region, int X, int Y, long seed) {
        Random rand = new Random(seed * ((X * Region.WIDTH) + Y)); // for location based randoms
        int[][] land = erodeArea(X, Y, rand);

        for (int y = Y; y < Y + Area.HEIGHT; y++) {
            for (int x = X; x < X + Area.WIDTH; x++) {
                int tileHeight = (int) Math.round((land[x - X][y - Y] + land[x - X][y - Y + 1] + land[x - X + 1][y - Y] + land[x - X + 1][y - Y + 1]) / 4.0);
                if (tileHeight < SEA_LEVEL) {
                    region.setTile(new Water(rand, tileHeight), x, y, 0); // add lakes, oceans
                } else {
                    int z = 0;
                    if (tileHeight >= SNOW_LEVEL) {
                        z = 5;
                    } else if (tileHeight >= TREE_LEVEL) {
                        z = 4;
                    } else if (tileHeight >= SLOPE_LEVEL) {
                        z = 3;
                    } else if (tileHeight >= MOUNTAIN_BASE_LEVEL) {
                        z = 2;
                    } else if (tileHeight >= HILL_LEVEL) {
                        z = 1;
                    }
                    if (tileHeight >= SNOW_LEVEL) {
                        region.setTile(new Snow(rand, tileHeight), x, y, z); // add land
                    } else {
                        region.setTile(new Grass(rand, tileHeight), x, y, z); // add land
                    }
                }
            }
        }
    }

    public void styleArea(Region region, int X, int Y, long seed) {
        Random rand = new Random(seed * ((X * Region.WIDTH) + Y)); // for location based randoms
        addBeaches(region, X, Y, rand);
        erode(region, X, Y, rand); // reduces tile Art errors by not allowing tiles to stand alone
        addSlopes(region, X, Y, rand);
    }

    public void populateArea(Region region, int X, int Y, long seed) {
        Random rand = new Random(seed * ((X * Region.WIDTH) + Y)); // for location based randoms
        addTreeClumps(region, X, Y, rand, 2, 6);

    }

    private void addBeaches(Region region, int X, int Y, Random rand) {
        for (int x = X; x < X + Area.WIDTH; x++) {
            for (int y = Y; y < Y + Area.HEIGHT; y++) {
                Tile tile = region.getTile(x, y, 0);
                if (tile instanceof LandTile) {
                    Tile[][] adjacent = region.getAdjacentTiles(x, y, 0);
                    int low = Integer.MAX_VALUE, high = 0;
                    for (Tile[] tiles : adjacent) {
                        for (Tile testTile : tiles) {
                            if (testTile != null) {
                                int height = testTile.getHeightInWorld();
                                if (height > high) {
                                    high = height;
                                } else if (height < low) {
                                    low = height;
                                }
                            }
                        }
                    }
                    int slope = high - low,
                            tileHeight = tile.getHeightInWorld();
                    if (tileHeight < BEACH_MAX_LEVEL && slope < 2) {
                        region.setTile(new Sand(rand, tileHeight), x, y, 0);
                    }
                }
            }
        }
    }

    private void addSlopes(Region region, int X, int Y, Random rand) {
        for (int x = X; x < X + Area.WIDTH; x++) {
            for (int y = Y; y < Y + Area.HEIGHT; y++) {
                int z = region.getTopLayerAt(x, y);
                Tile tile = region.getTile(x, y, z);
                if (rand.nextBoolean()) {
                    if (HILL_LEVEL - tile.getHeightInWorld() < 2 && HILL_LEVEL - tile.getHeightInWorld() > 0) {
                        region.setTile(new Ladder(rand, tile.getHeightInWorld(), 0, tile, 1, 0), x, y, z);
                    } else if (MOUNTAIN_BASE_LEVEL - tile.getHeightInWorld() < 2 && MOUNTAIN_BASE_LEVEL - tile.getHeightInWorld() > 0) {
                        region.setTile(new Ladder(rand, tile.getHeightInWorld(), 0, tile, 2, 1), x, y, z);
                    } else if (SLOPE_LEVEL - tile.getHeightInWorld() < 2 && SLOPE_LEVEL - tile.getHeightInWorld() > 0) {
                        region.setTile(new Ladder(rand, tile.getHeightInWorld(), 0, tile, 3, 2), x, y, z);
                    } else if (TREE_LEVEL - tile.getHeightInWorld() < 2 && TREE_LEVEL - tile.getHeightInWorld() > 0) {
                        region.setTile(new Ladder(rand, tile.getHeightInWorld(), 0, tile, 4, 3), x, y, z);
                    } else if (SNOW_LEVEL - tile.getHeightInWorld() < 2 && SNOW_LEVEL - tile.getHeightInWorld() > 0) {
                        region.setTile(new Ladder(rand, tile.getHeightInWorld(), 0, tile, 5, 4), x, y, z);
                    }
                }
            }
        }
    }

    private void erode(Region region, int X, int Y, Random rand) {
        for (int i = 0; i < 2; i++) { //number of times to erode
            for (int x = X; x < X + Area.WIDTH; x++) {
                for (int y = Y; y < Y + Area.HEIGHT; y++) {
                    Tile[][] adjacent = region.getAdjacentTiles(x, y, 0);
                    Tile tile = region.getTile(x, y, 0), topTile = null;
                    if (tile == null) {
                        continue;
                    }
                    int rank = tile.getRank(), count = 0;
                    if (adjacent[2][1] != null && adjacent[2][1].getRank() >= rank) {
                        count++;
                    } else if (adjacent[2][1] != null) {
                        topTile = adjacent[2][1];
                    }
                    if (adjacent[0][1] != null && adjacent[0][1].getRank() >= rank) {
                        count++;
                    } else if (topTile == null || adjacent[0][1] != null && adjacent[0][1].getRank() < topTile.getRank()) {
                        topTile = adjacent[0][1];
                    }
                    if (adjacent[1][2] != null && adjacent[1][2].getRank() >= rank) {
                        count++;
                    } else if (topTile == null || adjacent[1][2] != null && adjacent[1][2].getRank() < topTile.getRank()) {
                        topTile = adjacent[1][2];
                    }
                    if (adjacent[1][0] != null && adjacent[1][0].getRank() >= rank) {
                        count++;
                    } else if (topTile == null || adjacent[1][0] != null && adjacent[1][0].getRank() < topTile.getRank()) {
                        topTile = adjacent[1][0];
                    }
                    if (count < 2 && topTile != null) {
                        Tile newTile = topTile.newTile(rand, tile.getHeightInWorld());
                        region.setTile(newTile, x, y, 0);

                    }
                }
            }
        }
    }

    private void addTreeClumps(Region region, int X, int Y, Random rand, int min, int max) {
        int num = min + rand.nextInt(max - min + 1), count = 0, failed = 0;
        while (count < num) {
            int x = rand.nextInt(Area.WIDTH);
            int y = rand.nextInt(Area.HEIGHT);
            int z = region.getTopLayerAt(X, Y);
            if (region.getTile(X, Y, z) instanceof Grass && region.getTile(X, Y, z).getHeightInWorld() < TREE_LEVEL) {
                addTreeClump(region, X + x, Y + y, 5, 5, 3, 10, rand);
                count++;
            } else {
                failed++;
            }
            if (failed > 3 && count >= min){
                break;
            } else if (failed >= num && count < min) {
                break;
            }
        }
    }

    private void addTreeClump(Region region, int x, int y, int width, int height, int min, int max, Random rand) {
        int num = min + rand.nextInt(max - min + 1), count = 0, failed = 0;
        boolean oak = rand.nextBoolean();
        while (count < num) {
            int X = x - width/2 + rand.nextInt(width);
            int Y = y - height/2 + rand.nextInt(height);
            int Z = region.getTopLayerAt(X, Y);
            if (region.getTile(X, Y, Z) instanceof Grass && region.getTile(X, Y, Z).getHeightInWorld() < TREE_LEVEL && region.getEntitiesIntersecting(region.getTileBounds(X, Y, Z)).size() == 0) {
                if (oak) {
                    region.addEntity(new Tree(Tree.Type.Oak, X, Y, Z, rand));
                } else {
                    region.addEntity(new Tree(Tree.Type.Pine, X, Y, Z, rand));
                }
                //region.getTile(X,Y,Z).setObstructed(true);
                count++;
                failed = 0;
            } else {
                failed++;
            }
            if (failed > 5 && count >= min){
                break;
            } else if (failed >= width*height - 2) {
                break;
            }
        }
    }

    public int[][] getLandAreas() {
        return landAreas;
    }
}
