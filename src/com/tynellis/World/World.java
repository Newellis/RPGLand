package com.tynellis.World;

import com.tynellis.Entities.Entity;
import com.tynellis.Entities.EntityComparator;
import com.tynellis.Entities.moveEntity;
import com.tynellis.Save.FileHandler;
import com.tynellis.Save.SavedArea;
import com.tynellis.Save.StoreLoad;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

public class World implements Land, Serializable{
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 1024;
    public static boolean DEBUG = false;
    public WorldGen gen;
    private transient int NumOfAreasInWidth, NumOfAreasInHeight;
    private static final int Buffer = 2;
    private transient int[] areaOffset;
    private static final int X = 0,Y = 1;
    private transient Area[][] loadedAreas;
    private transient SortedSet<Entity> entities = new TreeSet<Entity>(new EntityComparator());
    //private transient ArrayList<Entity> entities = new ArrayList<Entity>(); // list of entities sorted by entities posY
    private transient ArrayList<Entity> entityMoveList = new ArrayList<Entity>();
    //private transient ArrayList<moveEntity> entityMoveList = new ArrayList<moveEntity>();

    public final long seed;
    public final Random WORLD_RAND;
    private String Name;

    public World(String name, long seed) {
        this.seed = seed;
        WORLD_RAND = new Random(seed);
        gen = new WorldGen(this);
        Name = name;
        FileHandler.setGameDir(name);
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        entityMoveList = new ArrayList<Entity>();
        //entityMoveList = new ArrayList<moveEntity>();
        entities = new TreeSet<Entity>(new EntityComparator());
        //entities = new ArrayList<Entity>();
    }

    //render world
    public void render(Graphics g, int width, int height, int XPos, int YPos, int ZPos) {
        int xOffset = (width / 2) - XPos;
        int yOffset = ((height / 2) - YPos);

        //calculate how many areas across to just past the edge
        NumOfAreasInWidth = 2 + (width / (Tile.WIDTH * Area.WIDTH));
        NumOfAreasInHeight = 2 + (height / (Tile.HEIGHT * Area.HEIGHT));
        //store old and find area offset
        int lastOffsetX = areaOffset[X], lastOffsetY = areaOffset[Y];
        setAreaOffset((XPos / (Tile.WIDTH * Area.WIDTH) - (NumOfAreasInWidth / 2)) - Buffer, (YPos / (Tile.HEIGHT * Area.HEIGHT) - (NumOfAreasInHeight / 2)) - Buffer);
        //load areas if old and new area offset are different
        loadAreas(lastOffsetX, lastOffsetY);
        //find Entities that are on the screen
        ArrayList<Entity> entitiesInView = getEntitiesNearBounds(new Rectangle(XPos - ((width + Area.WIDTH * Tile.WIDTH) / 2), YPos - ((height + Area.HEIGHT * Tile.HEIGHT) / 2), width + Area.WIDTH * Tile.WIDTH, height + Area.HEIGHT * Tile.HEIGHT));
        //render areas
        for (int j = Buffer; j <= Buffer + NumOfAreasInHeight; j++) {
            if (j < 0 || j > loadedAreas[0].length + Buffer) {
                continue;
            }
            for (int i = Buffer; i <= Buffer + NumOfAreasInWidth; i++) {
                if (i < 0 || i > loadedAreas.length + Buffer) {
                    continue;
                }
                if (loadedAreas[i][j] != null) {
                    loadedAreas[i][j].render(g, ZPos, (i + areaOffset[X]) * (Tile.WIDTH * Area.WIDTH) + xOffset, (j + areaOffset[Y]) * (Tile.HEIGHT * Area.HEIGHT) + yOffset);
                }
            }
        }

        //render entities
        if (entities.size() > 0) {
            for (Entity entity : entitiesInView) {
                entity.render(g, xOffset, yOffset);
                System.out.println(entity);
            }
        }
        System.out.println("\n\n");
    }

    public void tick() {
        //tick entities
        if (entities.size() > 0) {
            for (Entity entity: entities){
                entity.tick(this);
            }
        }

        //keep entities list sorted by entity posY
        if (entityMoveList.size() > 0){
            SortedSet<Entity> temp = new TreeSet<Entity>(new EntityComparator());
            temp.addAll(entities);
            entities = temp;
            entityMoveList.clear();
        }
    }

    //Load areas around player if center area has changed
    private void loadAreas(int lastX, int lastY){
        if (lastX != areaOffset[X] || lastY != areaOffset[Y]){ //if new areas need to be loaded
            shiftAreas(lastX - areaOffset[X], lastY - areaOffset[Y]);
            setLoadedAreas();
        }
    }

    //initialize all areas around center area
    public void loadAreas(){
        loadedAreas = new Area[NumOfAreasInWidth + 2*Buffer][NumOfAreasInHeight + 2*Buffer];
        setLoadedAreas();
    }

    //fills in any null spaces in loadedAreas and populates them
    private void setLoadedAreas() {
        for(int i = 0; i < loadedAreas.length; i++){
            int length = loadedAreas[i].length;
            for (int j = 0; j < length; j++){
                if (loadedAreas[i][j] == null) {
                    //load area from memory
                    SavedArea load = StoreLoad.LoadArea(areaOffset[X] + i, areaOffset[Y] + j);
                    if (load != null) { //if found set area to loaded area
                        loadedAreas[i][j] = load.getArea();
                        load.addEntitiesTo(this);
                    } else {            //else generate new area
                        loadedAreas[i][j] = new Area(WORLD_RAND);
                        gen.fillArea((i + areaOffset[X]) * Area.WIDTH, (j + areaOffset[Y]) * Area.HEIGHT, seed);
                    }
                }
            }
        }
        for(int i = 1; i < loadedAreas.length - 1; i++){
            int length = loadedAreas[i].length - 1;
            for (int j = 1; j < length; j++){
                if (loadedAreas[i][j] != null) {
                    if(loadedAreas[i][j].shouldUpdateArt()) {
                        Area[][] adjacent = getAdjacentAreas(i, j);
                        loadedAreas[i][j].updateLayerArt(adjacent);
                    }
                    if (loadedAreas[i][j].shouldPopulate()){
                        gen.populateArea((i + areaOffset[X]) * Area.WIDTH, (j + areaOffset[Y]) * Area.HEIGHT, seed);
                        loadedAreas[i][j].Populate();
                    }
                }
            }
        }
    }

    //shifts areas in loadedAreas so as to not have to reload every area
    private void shiftAreas(int x, int y) {
        Area[][] areas = new Area[NumOfAreasInWidth + 2*Buffer][NumOfAreasInHeight + 2*Buffer];
        if (x != 0) {
            for (int i = 0; i < loadedAreas.length; i++){
                if (i + x < areas.length && i + x >= 0) {
                    int length = areas[i+x].length;
                    if (areas[i+x].length > loadedAreas[i].length){
                        length = loadedAreas[i].length;
                    }
                    System.arraycopy(loadedAreas[i], 0, areas[i+x], 0 , length);
                } else {
                    for(int j = 0; j < loadedAreas[i].length; j++) {
                        if (loadedAreas[i][j] != null) {
                            StoreLoad.StoreRemovedArea(loadedAreas[i][j], this, areaOffset[X] + i + x, areaOffset[Y] + j);
                        }
                    }
                }
            }
            loadedAreas = areas;
        }
        if (y != 0) {
            for (int i = 0; i < loadedAreas.length; i++) {
                int rowLength = loadedAreas[i].length;
                if (i < areas.length) {
                    for (int j = 0; j < rowLength; j++) {
                        if (j + y < areas[i].length && j + y >= 0) {
                            areas[i][j+y] = loadedAreas[i][j];
                        } else if (loadedAreas[i][j] != null) {
                            StoreLoad.StoreRemovedArea(loadedAreas[i][j], this, areaOffset[X] + i, areaOffset[Y] + j + y);
                        }
                    }
                }
            }
            loadedAreas = areas;
        }
    }

    //get areas adjacent to the coordinates X, Y
    public Area[][] getAdjacentAreas(int X, int Y) {
        Area[][] adjacentAreas = new Area[3][3];
        for(int x = 0; x < adjacentAreas.length; x++) {
            for (int y = 0; y < adjacentAreas[x].length; y++) {
                if (X + (x - 1) >= 0 && Y + (y - 1) >= 0 && X + (x - 1) < WIDTH && Y + (y - 1) < HEIGHT && loadedAreas[X + (x - 1)][Y + (y - 1)] != null) {
                    adjacentAreas[x][y] = loadedAreas[X + (x - 1)][Y + (y - 1)];
                }
            }
        }
        return adjacentAreas;
    }

    //add an entity to the world
    public void addEntity(Entity e) {
        entities.add(e);
    }

    //remove an entity from the world
    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    //add an entity to the list to resort in the entities list
    public void addMoveEntity(Entity e){
        entityMoveList.add(e);
    }

    //get the tile at coordinates X, Y, Z
    public Tile getTile(int X, int Y, int Z) {
        if (X < 0 || Y < 0 || Z < 0 || X > WIDTH * Area.WIDTH || Y > HEIGHT * Area.HEIGHT || Z >= Area.DEPTH) {
            return null;
        }
        if (loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]][(Y / Area.HEIGHT) - areaOffset[World.Y]] != null){
            return loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]][(Y / Area.HEIGHT) - areaOffset[World.Y]].getTile(X % Area.WIDTH, Y % Area.HEIGHT, Z);
        }
        return null;
    }

    //set the tile at coordinates X, Y, Z
    public void setTile(Tile tile, int X, int Y, int Z){
        loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]][(Y / Area.HEIGHT) - areaOffset[World.Y]].setTile(tile, X % Area.WIDTH, Y % Area.HEIGHT, Z);
    }

    //get the bounds of the tile at coordinates X, Y, Z
    public Rectangle getTileBounds(int x, int y, int z) {
        Rectangle Bounds = getTile(x, y, z).getBounds();
        Bounds.setLocation(x * Tile.WIDTH, y * Tile.HEIGHT);
        return Bounds;
    }

    //get the bounds of the area at coordinates X, Y
    public Rectangle getAreaBounds(int x, int y) {
        Rectangle Bounds = Area.getBounds();
        Bounds.setLocation(x * Area.WIDTH * Tile.WIDTH, y * Area.HEIGHT * Tile.HEIGHT);
        return Bounds;
    }

    //get a list of Entities near Entity e
    public ArrayList<Entity> getEntitiesNearEntity(Entity e) {
        return getEntitiesNearEntity(e, 1);
    }

    public ArrayList<Entity> getEntitiesNearEntity(Entity e, int radius) {
        Rectangle rect = new Rectangle(e.getBounds());
        ArrayList<Entity> near = getEntitiesNearBounds(rect, radius);
        near.remove(e);//remove e from list to skip its self
        return near;
    }

    //get a list of Entities near Rectangle rect
    public ArrayList<Entity> getEntitiesNearBounds(Rectangle rect) {
        Rectangle nodeRect = new Rectangle();
        nodeRect.setLocation((int)(rect.getX()/Tile.WIDTH), (int)(rect.getY()/Tile.HEIGHT));
        nodeRect.height = rect.height/Tile.HEIGHT;
        nodeRect.width = rect.width/Tile.WIDTH;
        return getEntitiesNearBounds(rect, 1);
    }
    public ArrayList<Entity> getEntitiesNearBounds(Rectangle rect, int radius) {
        rect.grow(radius * Tile.WIDTH, radius * Tile.HEIGHT);
        return getEntitiesIntersecting(rect);
    }

    //get a list of Entities intersecting Rectangle rect
    public ArrayList<Entity> getEntitiesIntersecting(Rectangle rect){
        ArrayList<Entity> near = new ArrayList<Entity>();
        if (entities.size() > 0) {
            for (Entity entity : entities) {
                if (rect.intersects(entity.getBounds())) {
                    near.add(entity);
                }
            }
        }
        return near;
    }

    //get a list of entities in Rectangle rect
    public ArrayList<Entity> getEntitiesInBounds(Rectangle rect) {
        ArrayList<Entity> near = new ArrayList<Entity>();
        if (entities.size() > 0) {
            for (Entity entity : entities) {
                Rectangle bounds = entity.getBounds();
                if (rect.contains(bounds.x, bounds.y)) {
                    near.add(entity);
                }
            }
        }
        return near;
    }

    public void setHalfNumOfAreas(int width, int height){
        NumOfAreasInWidth = width;
        NumOfAreasInHeight = height;
    }

    public ArrayList<Node> getAdjacentNodesFromTiles(int x, int y, int z, Entity e){
        ArrayList<Node> nodes = new ArrayList<Node>();
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0){
                    continue;
                }
                if (x+i >= 0 || y+j >= 0){
                    if (getTile(x+i,y+j,z).isPassableBy(e)) {
                        nodes.add(new Node(x + i, y + j));
                    }
                }
            }
        }
        return nodes;
    }

    //set centerArea and calculate areaOffset from it
    public void setAreaOffset(int x, int y) {
        areaOffset = new int[]{x,y};
    }

    //save loaded Areas
    public void saveLoadedAreas() {
        for(int i = 0; i < loadedAreas.length; i++){
            int length = loadedAreas[i].length;
            for(int j = 0; j < length; j++){
                StoreLoad.StoreArea(loadedAreas[i][j], this, areaOffset[X] + i, areaOffset[Y] + j);
            }
        }
    }

    public String getName() {
        return Name;
    }

    public Random getRand() {
        return WORLD_RAND;
    }
}
