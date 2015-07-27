package com.tynellis.World;

import com.tynellis.Entities.Tree;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Dirt;
import com.tynellis.World.Tiles.Grass;
import com.tynellis.World.Tiles.Water;

import java.io.Serializable;
import java.util.Random;

public class WorldGen implements Serializable{
    private World world;
    private int[][] landAreas;
    private int[][] caveAreas = new int[World.WIDTH][World.HEIGHT];

    public WorldGen(World world) {
        this.world = world;
        genCaves();
        genLand();
    }

    public void genLand() {
        int pow = 6;
        int[][] seedArea = seedArea(pow);
        landAreas = diamondStep(seedArea, World.WIDTH, 2*((World.WIDTH*Area.WIDTH)/(int)Math.pow(2,pow)), world.WORLD_RAND, true);//25
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
                } else if(world.WORLD_RAND.nextBoolean()){
                    seedArea[x][y] = world.WORLD_RAND.nextInt(50);
                }
                 else {
                    seedArea[x][y] = world.WORLD_RAND.nextInt(50);
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

    public void genCaves() {
        float chanceToBeLand = 0.42f;
        caveAreas = makeLand(caveAreas, chanceToBeLand);
        for (int i = 0; i < 5; i++){
            caveAreas = smoothLand(caveAreas, 3, 4);
        }
    }

    public int[][] makeLand(int[][] map, float chance) {
        for(int x = 0; x< map.length; x++){
            for(int y = 0; y< map[x].length; y++){
                if(world.WORLD_RAND.nextFloat() < chance){
                    map[x][y] = 1;
                }
            }
        }
        return map;
    }

    public int[][] smoothLand(int[][] oldMap, int deathLimit, int birthLimit) {
        int[][] newMap = new int[oldMap.length][oldMap[0].length];
        //Loop over each row and column of the map
        for(int x=0; x<oldMap.length; x++) {
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
    private int countAliveNeighbours(int[][] map, int x, int y){
        int count = 0;
        for(int i=-1; i<2; i++){
            for(int j=-1; j<2; j++){
                int neighbour_x = x+i;
                int neighbour_y = y+j;
                //If we're looking at the middle point
                if(i == 0 && j == 0){
                    //Do nothing, we don't want to add ourselves in!
                }
                //In case the index we're looking at it off the edge of the map
                else if(neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= map.length || neighbour_y >= map[0].length){
                    //count = count + 1;
                }
                //Otherwise, a normal check of the neighbour
                else if(map[neighbour_x][neighbour_y] > 0){
                    count = count + 1;
                }
            }
        }
        return count;
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

    public void fillArea(int X, int Y, long seed) {
        Random rand = new Random(seed * ((X * World.WIDTH) + Y)); // for location based randoms
        int[][] land = erodeArea(X, Y, rand);

        for (int y = Y; y < Y + Area.HEIGHT; y++) {
            for (int x = X; x < X + Area.WIDTH; x++) {
                for (int z = 0; z < Area.DEPTH; z++) {
                    if (z == 0 || world.WORLD_RAND.nextInt(1) == 1 && z == 1) {
                        int tileNum = (int)Math.round((land[x-X][y-Y] + land[x-X][y-Y + 1] + land[x-X + 1][y-Y] + land[x-X + 1][y-Y + 1]) / 4.0);
                        if (tileNum < 25) {
                            world.setTile(new Water(rand), x, y, z);
                        } else {
                            int check = rand.nextInt(10);
                            if (check <= 9) {
                                world.setTile(new Grass(rand), x, y, z);
                            } else {
                                world.setTile(new Dirt(rand), x, y, z);
                            }
                        }
                    }
                }
            }
        }
    }

    public void populateArea(int X, int Y, long seed){
        Random rand = new Random(seed * ((X * World.WIDTH) + Y)); // for location based randoms
        addTreeClumps(X, Y, rand, 2, 6);

    }

    private void addTreeClumps(int X, int Y, Random rand, int min, int max) {
        int num = min + rand.nextInt(max - min + 1), count = 0, failed = 0;
        while (count < num) {
            int x = rand.nextInt(Area.WIDTH);
            int y = rand.nextInt(Area.HEIGHT);
            if (world.getTile(X, Y, 0) instanceof Grass) {
                addTreeClump(X + x, Y + y, 5, 5, 3, 10, rand);
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

    private void addTreeClump(int x, int y, int width, int height, int min, int max, Random rand) {
        int num = min + rand.nextInt(max - min + 1), count = 0, failed = 0;
        boolean oak = rand.nextBoolean();
        while (count < num) {
            int X = x - width/2 + rand.nextInt(width);
            int Y = y - height/2 + rand.nextInt(height);
            if (world.getTile(X, Y, 0) instanceof Grass && world.getEntitiesIntersecting(world.getTileBounds(X, Y, 0)).size() == 0) {
                world.addEntity(new Tree(oak, X, Y, 0, rand));
                world.getTile(X,Y,0).setObstructed(true);
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

    public int[][] getCaveAreas() {
        return caveAreas;
    }

}
