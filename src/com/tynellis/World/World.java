package com.tynellis.World;

import com.tynellis.Debug;
import com.tynellis.GameComponent;
import com.tynellis.Save.FileHandler;
import com.tynellis.Save.SavedArea;
import com.tynellis.Save.StoreLoad;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Orginization.EntityComparator;
import com.tynellis.World.Entities.Orginization.EntityQuadTree;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.LandTiles.ConnectorTile;
import com.tynellis.World.Tiles.LandTiles.LayeredTile;
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
    public WorldGen gen;
    private int[] spawnPoint;
    private transient int NumOfAreasInWidth, NumOfAreasInHeight;
    private static final int Buffer = 2;
    private transient int[] areaOffset;
    private static final int X = 0,Y = 1;
    private transient Area[][] loadedAreas;
    private transient ArrayList<Entity> entities = new ArrayList<Entity>();
    //private transient ArrayList<Entity> entities = new ArrayList<Entity>(); // list of entities sorted by entities posY
    //private transient int entitiesMoved = 0;
    private transient ArrayList<Entity> entityMoveList = new ArrayList<Entity>(), deadEntities = new ArrayList<Entity>();
    public final long seed;
    public final Random WORLD_RAND;
    private String Name;
    private transient EntityQuadTree collisionTree;

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
        deadEntities = new ArrayList<Entity>();
        entities = new ArrayList<Entity>();
        //entities = new ArrayList<Entity>();
    }

    //render world
    public void render(Graphics g, int width, int height, int XPos, int YPos, int ZPos) {
        int xOffset = (width / 2) - XPos;
        int yOffset = ((height / 2) - YPos + ZPos);

        //calculate how many areas across to just past the edge
        NumOfAreasInWidth = 2 + (width / (Tile.WIDTH * Area.WIDTH));
        NumOfAreasInHeight = 2 + (height / (Tile.HEIGHT * Area.HEIGHT));
        //store old and find area offset
        int lastOffsetX = areaOffset[X], lastOffsetY = areaOffset[Y];
        setAreaOffset((XPos / (Tile.WIDTH * Area.WIDTH) - (NumOfAreasInWidth / 2)) - Buffer, (YPos / (Tile.HEIGHT * Area.HEIGHT) - (NumOfAreasInHeight / 2)) - Buffer);
        //load areas if old and new area offset are different
        loadAreas(lastOffsetX, lastOffsetY);
        //find Entities that are on the screen
        SortedSet<Entity> entitiesToRender = new TreeSet<Entity>(new EntityComparator());
        entitiesToRender.addAll(getEntitiesNearBounds(new Rectangle(XPos - ((width + Area.WIDTH * Tile.WIDTH) / 2), YPos - ((height + Area.HEIGHT * Tile.HEIGHT) / 2), width + Area.WIDTH * Tile.WIDTH, height + Area.HEIGHT * Tile.HEIGHT)));
        //render areas
        for (int j = Buffer + NumOfAreasInHeight; j >= Buffer; j--) {
            if (j < 0 || j > loadedAreas[0].length + Buffer) {
                continue;
            }
            for (int i = Buffer; i <= Buffer + NumOfAreasInWidth; i++) {
                if (i < 0 || i > loadedAreas.length + Buffer) {
                    continue;
                }//todo render entitys behind tiles they are behind
                loadedAreas[i][j].render(g, (i + areaOffset[X]) * (Tile.WIDTH * Area.WIDTH) + xOffset, (j + areaOffset[Y]) * (Tile.HEIGHT * Area.HEIGHT) + yOffset);

//                for (int depth = 0; depth < Area.DEPTH; depth++) {
//                    for (int row = Area.HEIGHT - 1; row >= 0; row--) {
//                        if (loadedAreas[i][j] != null) {
//                            loadedAreas[i][j].renderStrip(row, depth, g, (i + areaOffset[X]) * (Tile.WIDTH * Area.WIDTH) + xOffset, (j + areaOffset[Y]) * (Tile.HEIGHT * Area.HEIGHT) + yOffset);
//                        }
//                    }
//                }
            }
        }

        //render entities
        if (entities.size() > 0) {
            for (Entity entity : entitiesToRender) {
                entity.render(g, xOffset, yOffset);
            }
        }
        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.COLLISION)) {
            if (collisionTree != null) {
                collisionTree.render(g, xOffset, yOffset);
            }
        }
    }

    public void tick() {
        //tick entities
        if (entities.size() > 0) {
            collisionTree = new EntityQuadTree(0, new Rectangle(areaOffset[X] * Area.WIDTH * Tile.WIDTH, areaOffset[Y] * Area.HEIGHT * Tile.HEIGHT, loadedAreas.length * Area.WIDTH * Tile.WIDTH, loadedAreas[0].length * Area.HEIGHT * Tile.HEIGHT));
            for (Entity entity : entities) {
                if (!entity.isDead()) {
                    collisionTree.insert(entity);
                }
            }
            for (Entity entity : entities) {
                if (entity.isDead()) {
                    deadEntities.add(entity);
                } else {

                    List<Entity> near = collisionTree.retrieve(new ArrayList<Entity>(), entity.getBounds());
                    near.remove(entity);
                    entity.tick(this, near);
                }
            }
        }
//        //keep entities list sorted by entity posY
//        if (entityMoveList.size() > 0) {
////            for (Entity entity: entityMoveList){
////                entities.remove(entity);
////                entities.add(entity);
////            }
//            SortedSet<Entity> temp = new TreeSet<Entity>(new EntityComparator());
//            temp.addAll(entities);
//            entities = temp;
//            entityMoveList.clear();
//        }
        //remove entities that have died
        if (deadEntities.size() > 0) {
            for (Entity entity : deadEntities) {
                entity.performDeath(this);
                entities.remove(entity);
            }
            deadEntities.clear();
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
                    if (loadedAreas[i][j].shouldPopulate()){
                        gen.styleWorld((i + areaOffset[X]) * Area.WIDTH, (j + areaOffset[Y]) * Area.HEIGHT, seed);
                        gen.populateArea((i + areaOffset[X]) * Area.WIDTH, (j + areaOffset[Y]) * Area.HEIGHT, seed);
                        loadedAreas[i][j].Populate();
                        for (Area[] areas : getAdjacentAreas(i, j)) {
                            for (Area area : areas) {
                                area.shouldUpdateArt(true);
                            }
                        }
                    }
                    if (loadedAreas[i][j].shouldUpdateArt()) {
                        Area[][] adjacent = getAdjacentAreas(i, j);
                        loadedAreas[i][j].updateLayerArt(adjacent);
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
        if ((X / Area.WIDTH) - areaOffset[World.X] < loadedAreas.length &&
                (X / Area.WIDTH) - areaOffset[World.X] >= 0 &&
                (Y / Area.HEIGHT) - areaOffset[World.Y] >= 0 &&
                (Y / Area.HEIGHT) - areaOffset[World.Y] < loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]].length &&
                loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]][(Y / Area.HEIGHT) - areaOffset[World.Y]] != null) {
            Tile tile = loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]][(Y / Area.HEIGHT) - areaOffset[World.Y]].getTile(X % Area.WIDTH, Y % Area.HEIGHT, Z);
            if (tile != null) {
                return tile;
            } else if (Z - 1 >= 0) {
                tile = loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]][(Y / Area.HEIGHT) - areaOffset[World.Y]].getTile(X % Area.WIDTH, Y % Area.HEIGHT, Z - 1);
                if (tile instanceof LayeredTile && ((LayeredTile) tile).isFull()) {
                    return tile;
                }
            }
        }
        return null;
    }

    //set the tile at coordinates X, Y, Z
    public void setTile(Tile tile, int X, int Y, int Z){
        loadedAreas[(X / Area.WIDTH) - areaOffset[World.X]][(Y / Area.HEIGHT) - areaOffset[World.Y]].setTile(tile, X % Area.WIDTH, Y % Area.HEIGHT, Z);
    }

    public Tile[][] getAdjacentTiles(int x, int y, int z) {
        Tile[][] adjacent = new Tile[3][3];
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int Z = getTopLayerAt(x, y);
                adjacent[i + 1][j + 1] = getTile(x + i, y + j, Z);
            }
        }
        return adjacent;
    }

    public ArrayList<Tile> getTilesIntersectingRect(Rectangle rectangle, int Z) {
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        rectangle.setLocation(rectangle.x, rectangle.y + (int) (3 * (Z / 4.0) * Tile.HEIGHT));
        for (int x = (int) rectangle.getX() / Tile.WIDTH; x < Math.ceil(rectangle.getX() + rectangle.width) / Tile.WIDTH; x++) {
            for (int y = (int) rectangle.getY() / Tile.HEIGHT; y < Math.ceil(rectangle.getY() + rectangle.height) / Tile.HEIGHT; y++) {
                if (getTile(x, y, Z) != null) {
                    tiles.add(getTile(x, y, Z));
                } else if (Z - 1 >= 0 && getTile(x, y, Z - 1) != null && getTile(x, y, Z - 1) instanceof LayeredTile && ((LayeredTile) getTile(x, y, Z - 1)).isFull()) {
                    tiles.add(getTile(x, y, Z - 1));
                } else {
                    tiles.add(null);
                }
            }
        }
        return tiles;
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

    public ArrayList<Entity> getEntitiesNearEntity(Entity e, double radius) {
        Rectangle rect = new Rectangle(e.getBounds());
        ArrayList<Entity> near = getEntitiesNearBounds(rect, radius);
        near.remove(e);//remove e from list to skip its self
        return near;
    }

    //get a list of Entities near Rectangle rect
    private ArrayList<Entity> getEntitiesNearBounds(Rectangle rect) {
        Rectangle nodeRect = new Rectangle();
        nodeRect.setLocation((int)(rect.getX()/Tile.WIDTH), (int)(rect.getY()/Tile.HEIGHT));
        nodeRect.height = rect.height/Tile.HEIGHT;
        nodeRect.width = rect.width/Tile.WIDTH;
        return getEntitiesNearBounds(rect, 1);
    }

    //get list of Entities in radius of rect
    public ArrayList<Entity> getEntitiesNearBounds(Rectangle rect, double radius) {
        rect.grow((int) (radius * Tile.WIDTH), (int) (radius * Tile.HEIGHT));
        return getEntitiesIntersecting(rect);
    }

    //get a list of Entities intersecting Rectangle rect
    public ArrayList<Entity> getEntitiesIntersecting(Rectangle rect){
        ArrayList<Entity> intersecting = new ArrayList<Entity>();
        if (collisionTree != null) {
            List<Entity> near = collisionTree.retrieve(new ArrayList<Entity>(), rect);
            for (Entity entity : near) {
                if (rect.intersects(entity.getBounds())) {
                    intersecting.add(entity);
                }
            }
        }
//        ArrayList<Entity> intersecting = new ArrayList<Entity>();
//        if (collisionTree != null) {
//            if (entities.size() > 0) {
//                for (Entity entity : entities) {
//                    if (rect.intersects(entity.getBounds())) {
//                        intersecting.add(entity);
//                    }
//                }
//            }
//        }
        return intersecting;
    }

    //get a list of entities in Rectangle rect
    public ArrayList<Entity> getEntitiesInBounds(Rectangle rect) {
        ArrayList<Entity> inBounds = new ArrayList<Entity>();
        if (collisionTree != null) {
            List<Entity> near = collisionTree.retrieve(new ArrayList<Entity>(), rect);
            for (Entity entity : near) {
                Rectangle bounds = entity.getBounds();
                if (rect.contains(bounds.x, bounds.y)) {
                    inBounds.add(entity);
                }
            }
        }
        return inBounds;
    }

    public void setHalfNumOfAreas(int width, int height){
        NumOfAreasInWidth = width;
        NumOfAreasInHeight = height;
    }

    public ArrayList<Node> getAdjacentNodesFromTiles(int x, int y, int z, Entity e){
        ArrayList<Node> nodes = new ArrayList<Node>();
        Tile centerTile = getTile(x, y, z);
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                if (i == 0 && j == 0){
                    continue;
                }
                if (x+i >= 0 || y+j >= 0){
                    Tile tile = getTile(x + i, y + j, z);
                    if (Math.abs(i) == 1 && Math.abs(j) == 1) {
                        if (!(centerTile instanceof ConnectorTile)) {
                            if (tile != null && tile.isPassableBy(e) && getTile(x + i, y, z) != null && getTile(x + i, y, z).isPassableBy(e) && !(getTile(x + i, y, z) instanceof ConnectorTile) && getTile(x, y + j, z) != null && !(getTile(x, y + j, z) instanceof ConnectorTile) && getTile(x, y + j, z).isPassableBy(e)) {
                                if (!(tile instanceof ConnectorTile)) {
                                    nodes.add(new Node(x + i, y + j, z));
                                }
                            }
                        }
                    } else if (tile != null && tile.isPassableBy(e)) {
                        if (tile instanceof ConnectorTile && ((ConnectorTile) tile).isFull() && ((ConnectorTile) tile).canUse(e) && ((ConnectorTile) tile).getDirection() % 2 == Math.abs(i)) {
                            nodes.add(new Node(x + i, y + j, ((ConnectorTile) tile).getHeight()));
                        } else if (!(tile instanceof ConnectorTile)) {
                            if (centerTile instanceof ConnectorTile && ((ConnectorTile) centerTile).isFull() && ((ConnectorTile) centerTile).canUse(e) && ((ConnectorTile) centerTile).getDirection() % 2 == Math.abs(i)) {
                                nodes.add(new Node(x + i, y + j, z));
                            } else if (!(centerTile instanceof ConnectorTile)) {
                                nodes.add(new Node(x + i, y + j, z));
                            }
                        }
                    }
                }
            }
        }
        return nodes;
    }

    public ArrayList<Node> getAdjacentNodes(Node node, Entity e) {
        if (node.getX() == Math.floor(node.getX()) && node.getY() == Math.floor(node.getY())) {
            if (node.getZ() % 1.0 == .5) {
                ArrayList<Node> twoLayers = new ArrayList<Node>(getAdjacentNodesFromTiles((int) node.getX(), (int) node.getY(), (int) node.getZ(), e));
                twoLayers.addAll(getAdjacentNodesFromTiles((int) node.getX(), (int) node.getY(), (int) Math.ceil(node.getZ()), e));
                return twoLayers;
            }
            return getAdjacentNodesFromTiles((int) node.getX(), (int) node.getY(), (int) node.getZ(), e);
        } else {
            ArrayList<Node> nodes = new ArrayList<Node>();
            Node[] adjacent = new Node[4];
            if (getTile((int) Math.floor(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ())) != null && getTile((int) Math.floor(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ())).isPassableBy(e)) {
                adjacent[0] = new Node((int) Math.floor(node.getX()), (int) Math.floor(node.getY()), node.getZ());
            }
            if (getTile((int) Math.floor(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ())) != null && getTile((int) Math.floor(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ())).isPassableBy(e)) {
                adjacent[1] = new Node((int) Math.floor(node.getX()), (int) Math.ceil(node.getY()), node.getZ());
            }
            if (getTile((int) Math.ceil(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ())) != null && getTile((int) Math.ceil(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ())).isPassableBy(e)) {
                adjacent[2] = new Node((int) Math.ceil(node.getX()), (int) Math.floor(node.getY()), node.getZ());
            }
            if (getTile((int) Math.ceil(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ())) != null && getTile((int) Math.ceil(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ())).isPassableBy(e)) {
                adjacent[3] = new Node((int) Math.ceil(node.getX()), (int) Math.ceil(node.getY()), node.getZ());
            }
            for (Node adj : adjacent) {
                if (adj != null) {
                    nodes.add(adj);
                }
            }
            return nodes;
        }
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

    public int getTopLayerAt(int x, int y) {
        return getTopLayerAtBelow(x, y, Area.DEPTH);
    }

    public int getTopLayerAtBelow(int x, int y, int z) {
        int top;
        for (int i = z; i >= 0; i--) {
            if (getTile(x, y, i) != null) {
                top = i;
                return top;
            }
        }
        return 0;
    }

    public double getTileHeightAt(double x, double y, double z) {
        if (getTile((int) x, (int) y, getTopLayerAtBelow((int) x, (int) y, (int) z)) != null) {
            return ((int) z) + getTile((int) x, (int) y, getTopLayerAtBelow((int) x, (int) y, (int) z)).getHeightDeviationAt(x - (int) x, y - (int) y);
        }
        return z;
    }

    public int[] getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(int[] spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public void genSpawn(long seed) {
        gen.setSpawn(seed);
    }
}
