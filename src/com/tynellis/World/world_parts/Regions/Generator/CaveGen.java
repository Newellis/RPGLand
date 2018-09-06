package com.tynellis.World.world_parts.Regions.Generator;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Rocks.CopperOre;
import com.tynellis.World.Entities.Rocks.GoldOre;
import com.tynellis.World.Entities.Rocks.IronOre;
import com.tynellis.World.Entities.Rocks.Rock;
import com.tynellis.World.Entities.Rocks.SilverOre;
import com.tynellis.World.Entities.Rocks.TinOre;
import com.tynellis.World.Tiles.LandTiles.Natural.CaveWall;
import com.tynellis.World.Tiles.LandTiles.Natural.Dirt;
import com.tynellis.World.Tiles.LandTiles.Natural.LavaRock;
import com.tynellis.World.Tiles.LandTiles.Natural.Stone;
import com.tynellis.World.World;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Regions.Region;

import java.io.Serializable;
import java.util.Random;

public class CaveGen extends WorldGen implements Serializable {
    private World world;
    private int depth;

    public CaveGen(World world, int depth) {
        this.world = world;
        this.depth = depth;
//        genCaves();
    }

//    public void genCaves() {
//        float chanceToBeLand = 0.4f;//0.42f;
//        caveAreas = makeLand(caveAreas, chanceToBeLand);
//        for (int i = 0; i < 5; i++){
//            caveAreas = smoothLand(caveAreas, 3, 4);
//        }
//    }

    public int[][] makeLand(int[][] map, float chance) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (world.getRand().nextFloat() < chance) {
                    map[x][y] = 1;
                }
            }
        }
        return map;
    }

    public int[][] smoothLand(int[][] oldMap, int deathLimit, int birthLimit) {
        int[][] newMap = new int[oldMap.length][oldMap[0].length];
        //Loop over each row and column of the map
        for (int x = 0; x < oldMap.length; x++) {
            for (int y = 0; y < oldMap[0].length; y++) {
                int nbs = countAliveNeighbours(oldMap, x, y);
                //The new value is based on our simulation rules
                //First, if a cell is alive but has too few neighbours, kill it.
                if (oldMap[x][y] == 1) {
                    if (nbs < deathLimit) {
                        newMap[x][y] = 0;
                    } else {
                        newMap[x][y] = 1;
                    }
                } //Otherwise, if the cell is dead now, check if it has the right number of neighbours to be 'born'
                else {
                    if (nbs > birthLimit) {
                        newMap[x][y] = 1;
                    } else {
                        newMap[x][y] = 0;
                    }
                }
            }
        }
        return newMap;
    }

    //Returns the number of cells in a ring around (x,y) that are alive.
    private int countAliveNeighbours(int[][] map, int x, int y) {
        int count = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int neighbour_x = x + i;
                int neighbour_y = y + j;
                //If we're looking at the middle point
                if (i != 0 || j != 0) {
                    if (neighbour_x >= 0 && neighbour_y >= 0 && neighbour_x < map.length && neighbour_y < map[0].length) {
                        if (map[neighbour_x][neighbour_y] > 0) {
                            count = count + 1;
                        }
                    }
                }
            }
        }
        return count;
    }

    @Override
    public void fillArea(Region region, int X, int Y, long seed) {
        Random rand = new Random(seed * ((X * Region.WIDTH) + Y)); // for location based randoms
        int[][] land = makeCaves(X / Area.WIDTH, Y / Area.HEIGHT, seed, 0.5);
        boolean deep = false;
        double tileChance = 0.3 + (0.1 * depth);
        if (tileChance > 1.3) {
            tileChance -= 1;
            deep = true;
        }
        int[][] tile = makeCaves(X / Area.WIDTH, Y / Area.HEIGHT, seed, tileChance);
        for (int y = Y; y < Y + Area.HEIGHT; y++) {
            for (int x = X; x < X + Area.WIDTH; x++) {
                if (land[x - X][y - Y] == 1) {
                    if (tile[x - X][y - Y] == 1) {
                        region.setTile((deep) ? new LavaRock(rand, 100) : new Stone(rand, 100), x, y, 0);
                    } else {
                        region.setTile((deep) ? new Stone(rand, 100) : new Dirt(rand, 100), x, y, 0);
                    }
                } else {
                    region.setTile(new CaveWall(rand, 100), x, y, 0);
                }
            }
        }
        erode(region, X, Y, rand); // reduces tile Art errors by not allowing tiles to stand alone
    }

    private int[][] makeCaves(int x, int y, long seed, double chance) {
        int[][] areas = fillAreas(x, y, seed, chance);//Chance of being cave
        for (int i = 0; i < 10; i++) {
            areas = smoothLand(areas, 3, 4);//rate of death and birth
        }
        int[][] area = new int[Area.WIDTH][Area.HEIGHT];
        for (int i = 0; i < Area.WIDTH; i++) {
            System.arraycopy(areas[Area.WIDTH + i], Area.HEIGHT, area[i], 0, Area.HEIGHT);
        }
        return area;
    }

    private int[][] fillAreas(int X, int Y, long seed, double chance) {
        int[][] areas = new int[Area.WIDTH * 3][Area.HEIGHT * 3];
        for (int xOff = -1; xOff < 2; xOff++) {
            for (int yOff = -1; yOff < 2; yOff++) {
                Random rand = new Random(seed * (((X + xOff) * Region.WIDTH) + (Y + yOff))); // for location based randoms
                for (int i = 0; i < Area.WIDTH; i++) {
                    for (int j = 0; j < Area.HEIGHT; j++) {
                        if (rand.nextFloat() < chance) {
                            areas[((1 + xOff) * Area.WIDTH) + i][((1 + yOff) * Area.HEIGHT) + j] = 1;
                        }
                    }
                }
            }
        }
        return areas;
    }

    @Override
    public void styleArea(Region region, int x, int y, long seed) {

    }

    @Override
    public void populateArea(Region region, int x, int y, long seed) {
        Random rand = new Random(seed * ((x * Region.WIDTH) + y)); // for location based randoms
        addRockClumps(region, x, y, rand, 1 + (depth / 6), 4 + (depth / 2));
    }

    private void addRockClumps(Region region, int X, int Y, Random rand, int min, int max) {
        int num = min + rand.nextInt(max - min + 1), count = 0, failed = 0;
        while (count < num) {
            int x = rand.nextInt(Area.WIDTH);
            int y = rand.nextInt(Area.HEIGHT);
            int z = region.getTopLayerAt(X, Y);
            if (!(region.getTile(X, Y, z) instanceof CaveWall)) {
                addRockClump(region, X + x, Y + y, 5 + depth, 5 + depth, 1 + (depth / 10), 5 + (depth / 3), rand);
                count++;
            } else {
                failed++;
            }
            if (failed > 3 && count >= min) {
                break;
            } else if (failed >= num && count < min) {
                break;
            }
        }
    }

    private void addRockClump(Region region, int x, int y, int width, int height, int min, int max, Random rand) {
        int num = min + rand.nextInt(max - min + 1), count = 0, failed = 0;
        while (count < num) {
            int X = x - width / 2 + rand.nextInt(width);
            int Y = y - height / 2 + rand.nextInt(height);
            int Z = region.getTopLayerAt(X, Y);
            if (!(region.getTile(X, Y, Z) instanceof CaveWall) && region.getEntitiesIntersecting(region.getTileBounds(X, Y, Z)).size() == 0) {
                region.addEntity(getRock(X, Y, Z, rand));
                count++;
                failed = 0;
            } else {
                failed++;
            }
            if (failed > 5 && count >= min) {
                break;
            } else if (failed >= width * height - 2) {
                break;
            }
        }
    }

    private Entity getRock(int x, int y, int z, Random rand) {
        if (rand.nextDouble() < .3) {
            return new CopperOre(x, y, z, rand);
        } else if (rand.nextDouble() < .5) {
            return new TinOre(x, y, z, rand);
        }
        if (rand.nextDouble() < 0.2 * (depth - 3)) {
            return new GoldOre(x, y, z, rand);
        }
        if (rand.nextDouble() < 0.1 * (depth - 4)) {
            return new SilverOre(x, y, z, rand);
        }
        if (rand.nextDouble() < 0.4 * (depth - 5)) {
            return new IronOre(x, y, z, rand);
        }
        return new Rock(x, y, z, rand);
    }
}
