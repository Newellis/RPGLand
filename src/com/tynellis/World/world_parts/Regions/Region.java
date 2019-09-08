package com.tynellis.World.world_parts.Regions;

import com.tynellis.GameComponent;
import com.tynellis.Save.SavedArea;
import com.tynellis.Save.StoreLoad;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.Player;
import com.tynellis.World.Entities.Orginization.EntityComparator;
import com.tynellis.World.Entities.Orginization.EntityQuadTree;
import com.tynellis.World.Light.LightOverlay;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.LandTiles.ConnectorTile;
import com.tynellis.World.Tiles.LandTiles.LayeredTile;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.World.spawners.WorldSpawner;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Land;
import com.tynellis.World.world_parts.Regions.Generator.WorldGen;
import com.tynellis.debug.Debug;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.*;

public abstract class Region implements Serializable, Land {
    public static final int WIDTH = 1024;
    public static final int HEIGHT = 1024;
    protected String name;
    private WorldGen gen;
    private transient Area[][] loadedAreas = new Area[1][1];
    private transient ArrayList<Entity> entities;
    private transient ArrayList<Entity> entityMoveList = new ArrayList<Entity>(), deadEntities = new ArrayList<Entity>(), newEntities = new ArrayList<Entity>();
    private transient EntityQuadTree collisionTree;
    private transient int[] areaOffset;

    protected WorldSpawner worldSpawner = new WorldSpawner(5);
    private ArrayList<Rectangle> spawnFreeAreas = new ArrayList<Rectangle>();

    private transient LightOverlay lighting = new LightOverlay();

    public Region(String name, WorldGen gen) {
        this.name = name;
        this.gen = gen;
        addEntitiesToSpawn();
        entities = new ArrayList<Entity>();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        entityMoveList = new ArrayList<Entity>();
        deadEntities = new ArrayList<Entity>();
        newEntities = new ArrayList<Entity>();
        loadedAreas = new Area[1][1];
        areaOffset = new int[2];
        collisionTree = new EntityQuadTree(0, getLoadedAreaBounds());
        lighting = new LightOverlay();
        addEntitiesToSpawn();
        entities = new ArrayList<Entity>();
    }

    protected abstract void addEntitiesToSpawn();

    public void addSpawnFreeArea(Rectangle area) {
        spawnFreeAreas.add(area);
    }

    //render world
    public void render(Graphics g, int xOffset, int yOffset, Rectangle screen) {

        //find Entities that are on the screen
        SortedSet<Entity> entitiesToRender = new TreeSet<Entity>(new EntityComparator());
        synchronized (collisionTree) {
            entitiesToRender.addAll(getEntitiesNearBounds(screen));
        }
        //render areas
        for (int j = 2 * World.Buffer + (screen.height / (Tile.HEIGHT * Area.HEIGHT)); j >= World.Buffer; j--) {
            if (j < 0 || j > loadedAreas[0].length - 1) {
                continue;
            }
            for (int i = World.Buffer; i <= 2 * World.Buffer + (screen.width / (Tile.WIDTH * Area.WIDTH)); i++) {
                if (i < 0 || i > loadedAreas.length - 1) {
                    continue;
                }//todo render entities behind tiles they are behind
                synchronized (loadedAreas) {
                    if (loadedAreas[i][j] != null) {
                        loadedAreas[i][j].render(g, (i + areaOffset[World.X]) * (Tile.WIDTH * Area.WIDTH) + xOffset, (j + areaOffset[World.Y]) * (Tile.HEIGHT * Area.HEIGHT) + yOffset);
                    }
                }
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
                if (entity.getLight() != null) {
                    entity.getLight().render(g, xOffset, yOffset);
                }
            }
        }
        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.COLLISION)) {
            if (collisionTree != null) {
                collisionTree.render(g, xOffset, yOffset);
            }
        }
    }

    public synchronized void tick(Random random) {
        //tick entities
        worldSpawner.tick(this, random);
        if (newEntities.size() > 0) {
            entities.addAll(newEntities);
            System.out.println("add entities: " + entities);
            newEntities.clear();
        }
        synchronized (entities) {
            if (entities.size() > 0) {
                collisionTree = new EntityQuadTree(0, new Rectangle(areaOffset[World.X] * Area.WIDTH * Tile.WIDTH, areaOffset[World.Y] * Area.HEIGHT * Tile.HEIGHT, loadedAreas.length * Area.WIDTH * Tile.WIDTH, loadedAreas[0].length * Area.HEIGHT * Tile.HEIGHT));
                for (Entity entity : entities) {
                    if (!entity.isDead()) {
                        collisionTree.insert(entity);
                    } else {
                        deadEntities.add(entity);
                    }
                }
                for (Entity entity : entities) {
                    if (!entity.isDead()) {
                        List<Entity> near = collisionTree.retrieve(new ArrayList<Entity>(), entity.getBounds());
                        near.remove(entity);
                        entity.tick(this, random, near);
                        if (entity.getLight() != null) {
                            near = collisionTree.retrieve(new ArrayList<Entity>(), entity.getLight().getBounds());
                            near.remove(entity);
                            entity.getLight().tick(this, near);
                        }
                    }
                }
            }
        }
//        //keep entities list sorted by entity posY
//        if (entityMoveList.size() > 0) {
////            for (Entity entity: entityMoveList){
////                entities.remove(entity);
////                entities.add(entity);
////            }
        SortedSet<Entity> temp = new TreeSet<Entity>(new EntityComparator());
//            temp.addAll(entities);
//            entities = temp;
//            entityMoveList.clear();
//        }
        //remove entities that have died
        if (deadEntities.size() > 0) {
            for (Entity entity : deadEntities) {
                if (entity.isDead()) {
                    entity.performDeath(this, random);
                }
                if (entity instanceof Player) {
                    System.out.println("Remove player from " + this.getName());
                }
                entities.remove(entity);
                collisionTree.remove(entity);
            }
            deadEntities.clear();
        }
        updateAreas(random);
    }

    public Rectangle getLoadedAreaBounds() {
        return new Rectangle(areaOffset[World.X] * Area.WIDTH * Tile.WIDTH, areaOffset[World.Y] * Area.HEIGHT * Tile.HEIGHT, loadedAreas.length * Area.WIDTH * Tile.WIDTH, loadedAreas[0].length * Area.HEIGHT * Tile.HEIGHT);
    }


    //Load areas around player if center area has changed
    public synchronized void loadAreas(int lastX, int lastY, Rectangle loadedArea, Random rand, long seed) {

        boolean nullArea = false;
        for (int i = 0; i < loadedAreas.length; i++) {
            for (int j = 0; j < loadedAreas[i].length; j++) {
                if (loadedAreas[i][j] == null) {
                    nullArea = true;
                    break;
                }
            }
        }
        if (nullArea || (lastX != areaOffset[World.X] || lastY != areaOffset[World.Y]) || loadedAreas.length != loadedArea.width || loadedAreas[0].length != loadedArea.height) {
            synchronized (loadedAreas) {
                shiftAreas(loadedArea, lastX - areaOffset[World.X], lastY - areaOffset[World.Y]);
                setLoadedAreas(this, gen, rand, seed);
            }
        }
    }

    //initialize all areas around center area
    public synchronized void loadAreas(Rectangle loadedArea, Random rand, long seed) {
        synchronized (loadedAreas) {
            setAreaOffset(new int[]{loadedArea.x, loadedArea.y});
            loadedAreas = new Area[loadedArea.width][loadedArea.height];
            setLoadedAreas(this, gen, rand, seed);
        }
    }

    //fills in any null spaces in loadedAreas and populates them
    private void setLoadedAreas(Region region, WorldGen gen, Random rand, long seed) {
        for(int i = 0; i < loadedAreas.length; i++){
            int length = loadedAreas[i].length;
            for (int j = 0; j < length; j++){
                if (loadedAreas[i][j] == null) {
                    //load area from memory
                    SavedArea load = StoreLoad.LoadArea(this, areaOffset[World.X] + i, areaOffset[World.Y] + j);
                    if (load != null) { //if found set area to loaded area
                        loadedAreas[i][j] = load.getArea();
                        load.addEntitiesTo(this);
                    } else {            //else generate new area
                        loadedAreas[i][j] = new Area(rand);
                        gen.fillArea(region, (i + areaOffset[World.X]) * Area.WIDTH, (j + areaOffset[World.Y]) * Area.HEIGHT, seed);
                    }
                }
            }
        }
        for(int i = 1; i < loadedAreas.length - 1; i++){
            int length = loadedAreas[i].length - 1;
            for (int j = 1; j < length; j++){
                if (loadedAreas[i][j] != null) {
                    if (loadedAreas[i][j].shouldPopulate()){
                        gen.styleArea(this, (i + areaOffset[World.X]) * Area.WIDTH, (j + areaOffset[World.Y]) * Area.HEIGHT, seed);
                        gen.populateArea(this, (i + areaOffset[World.X]) * Area.WIDTH, (j + areaOffset[World.Y]) * Area.HEIGHT, seed);
                        loadedAreas[i][j].Populate();
                        for (Area[] areas : getAdjacentLoadedAreas(i, j)) {
                            for (Area area : areas) {
                                area.shouldUpdateArt(true);
                            }
                        }
                    }
                    if (loadedAreas[i][j].shouldUpdateArt()) {
                        Area[][] adjacent = getAdjacentLoadedAreas(i, j);
                        loadedAreas[i][j].updateLayerArt(adjacent);
                    }
                }
            }
        }
    }

    //shifts areas in loadedAreas so as to not have to reload every area
    private void shiftAreas(Rectangle loadedArea, int x, int y) {
        Area[][] areas = new Area[loadedArea.width + 2 * World.Buffer][loadedArea.height + 2 * World.Buffer];
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
                            StoreLoad.StoreRemovedArea(loadedAreas[i][j], this, areaOffset[World.X] + i + x, areaOffset[World.Y] + j);
                        }
                    }
                }
            }
            synchronized (loadedAreas) {
                loadedAreas = areas;
            }
        }
        if (y != 0) {
            for (int i = 0; i < loadedAreas.length; i++) {
                int rowLength = loadedAreas[i].length;
                if (i < areas.length) {
                    for (int j = 0; j < rowLength; j++) {
                        if (j + y < areas[i].length && j + y >= 0) {
                            areas[i][j+y] = loadedAreas[i][j];
                        } else if (loadedAreas[i][j] != null) {
                            StoreLoad.StoreRemovedArea(loadedAreas[i][j], this, areaOffset[World.X] + i, areaOffset[World.Y] + j + y);
                        }
                    }
                }
            }
            synchronized (loadedAreas) {
                loadedAreas = areas;
            }
        }
    }

    //save loaded Areas
    public void saveLoadedAreas() {
        for (int i = 0; i < loadedAreas.length; i++) {
            int length = loadedAreas[i].length;
            for (int j = 0; j < length; j++) {
                StoreLoad.StoreArea(loadedAreas[i][j], this, areaOffset[World.X] + i, areaOffset[World.Y] + j);
            }
        }
    }

    public void setAreaOffset(int[] areaOffset) {
        this.areaOffset = areaOffset;
    }

    //get areas adjacent to the coordinates X, Y
    public Area[][] getAdjacentLoadedAreas(int loadedX, int loadedY) {
        Area[][] adjacentAreas = new Area[3][3];
        for(int x = 0; x < adjacentAreas.length; x++) {
            for (int y = 0; y < adjacentAreas[x].length; y++) {
                if (loadedX + (x - 1) >= 0 && loadedY + (y - 1) >= 0 && loadedX + (x - 1) < loadedAreas.length && loadedY + (y - 1) < loadedAreas[loadedX + (x - 1)].length && loadedAreas[loadedX + (x - 1)][loadedY + (y - 1)] != null) {
                    adjacentAreas[x][y] = loadedAreas[loadedX + (x - 1)][loadedY + (y - 1)];
                }
            }
        }
        return adjacentAreas;
    }

    public void queueAdditionOfEntity(Entity e) {
        newEntities.add(e);
    }

    public void queueRemovalOfEntity(Entity e) {
        deadEntities.add(e);
    }

    //add an entity to the world
    public synchronized void addEntity(Entity e) {
        entities.add(e);
        if (collisionTree == null) {
            if (areaOffset == null) areaOffset = new int[2];
            collisionTree = new EntityQuadTree(0, new Rectangle(areaOffset[World.X] * Area.WIDTH * Tile.WIDTH, areaOffset[World.Y] * Area.HEIGHT * Tile.HEIGHT, loadedAreas.length * Area.WIDTH * Tile.WIDTH, loadedAreas[0].length * Area.HEIGHT * Tile.HEIGHT));
        }
        collisionTree.insert(e);
    }

    //remove an entity from the world
    public synchronized void removeEntity(Entity e) {
        entities.remove(e);
        collisionTree.remove(e);
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
                            if (tile != null && tile.isPassableBy(e) && !isTileObstructed(x + i, y + j, z) && getTile(x + i, y, z) != null && getTile(x + i, y, z).isPassableBy(e) && !isTileObstructed(x + i, y, z) && !(getTile(x + i, y, z) instanceof ConnectorTile) && getTile(x, y + j, z) != null && !(getTile(x, y + j, z) instanceof ConnectorTile) && getTile(x, y + j, z).isPassableBy(e) && !isTileObstructed(x, y + j, z)) {
                                if (!(tile instanceof ConnectorTile)) {
                                    nodes.add(new Node(x + i, y + j, z, tile));
                                }
                            }
                        }
                    } else if (tile != null && tile.isPassableBy(e) && !isTileObstructed(x + i, y + j, z)) {
                        if (tile instanceof ConnectorTile && ((ConnectorTile) tile).isFull() && ((ConnectorTile) tile).canUse(e) && ((ConnectorTile) tile).getDirection() % 2 == Math.abs(i)) {
                            nodes.add(new Node(x + i, y + j, ((ConnectorTile) tile).getHeight(), tile));
                        } else if (!(tile instanceof ConnectorTile)) {
                            if (centerTile instanceof ConnectorTile && ((ConnectorTile) centerTile).isFull() && ((ConnectorTile) centerTile).canUse(e) && ((ConnectorTile) centerTile).getDirection() % 2 == Math.abs(i)) {
                                nodes.add(new Node(x + i, y + j, z, tile));
                            } else if (!(centerTile instanceof ConnectorTile)) {
                                nodes.add(new Node(x + i, y + j, z, tile));
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
            Tile ne, nw, se, sw;
            ne = getTile((int) Math.ceil(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ()));
            nw = getTile((int) Math.floor(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ()));
            se = getTile((int) Math.ceil(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ()));
            sw = getTile((int) Math.floor(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ()));
            if (nw != null && nw.isPassableBy(e) && !isTileObstructed((int) Math.floor(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ()))) {
                adjacent[0] = new Node((int) Math.floor(node.getX()), (int) Math.floor(node.getY()), node.getZ(), nw);
            }
            if (sw != null && sw.isPassableBy(e) && !isTileObstructed((int) Math.floor(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ()))) {
                adjacent[1] = new Node((int) Math.floor(node.getX()), (int) Math.ceil(node.getY()), node.getZ(), sw);
            }
            if (ne != null && ne.isPassableBy(e) && !isTileObstructed((int) Math.ceil(node.getX()), (int) Math.floor(node.getY()), (int) Math.floor(node.getZ()))) {
                adjacent[2] = new Node((int) Math.ceil(node.getX()), (int) Math.floor(node.getY()), node.getZ(), ne);
            }
            if (se != null && se.isPassableBy(e) && !isTileObstructed((int) Math.ceil(node.getX()), (int) Math.ceil(node.getY()), (int) Math.floor(node.getZ()))) {
                adjacent[3] = new Node((int) Math.ceil(node.getX()), (int) Math.ceil(node.getY()), node.getZ(), se);
            }
            for (Node adj : adjacent) {
                if (adj != null) {
                    nodes.add(adj);
                }
            }
            return nodes;
        }
    }

    public boolean isTileObstructed(int x, int y, int z) {
        if (getTile(x, y, z) == null) {
            return true;
        }
        ArrayList<Entity> list = getEntitiesIntersecting(getTileBounds(x, y, z));
        for (Iterator<Entity> it = list.iterator(); it.hasNext(); ) {
            Entity next = it.next();
            if (next.isMoveable()) {
                it.remove();
            }
        }
        return list.size() > 0;
    }

    public boolean isTileCurrentlyObstructed(int x, int y, int z) {
        return isTileCurrentlyObstructedFor(null, x, y, z);
    }

    public boolean isTileCurrentlyObstructedFor(Entity e, int x, int y, int z) {
        if (getTile(x, y, z) == null) {
            return true;
        }
        ArrayList<Entity> list = getEntitiesIntersecting(getTileBounds(x, y, z));
        ArrayList<Entity> blocking = new ArrayList<Entity>();
        if (e != null) {
            list.remove(e);
            for (Entity entity : list) {
                if (!entity.isPassableBy(e)) {
                    blocking.add(entity);
                }
            }
        } else {
            blocking.addAll(list);
        }
        return blocking.size() > 0;
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

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }

    public ArrayList<Rectangle> getSpawnFreeAreas() {
        return spawnFreeAreas;
    }

    public WorldGen getGen() {
        return gen;
    }

    public int[] getLocationOfNearestTile(Class tileType, int x, int y, int z, int range) {
        for (int r = 0; r <= range; r++) {
            for (int i = -r; i <= x + r; i++) {
                for (int j = -r; j <= y + r; j++) {
                    for (int k = -1; k < 2; k++) {
                        Tile test = getTile(x + i, y + j, z + k);
                        if (test != null) {
                            if (tileType.isInstance(test) && !isTileObstructed(x + i, y + j, z + k)) {
                                return new int[]{x + i, y + j, z + k};
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void updateTileArtAt(int x, int y) {
        Tile[][] adjacent = new Tile[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                adjacent[i][j] = getTile(i - 1 + x, j - 1 + y, getTopLayerAt(i - 1 + x, j - 1 + y));
            }
        }
        Tile tile = getTile(x, y, getTopLayerAt(x, y));
        if (tile != null) {
            tile.updateArt(adjacent);
        }
    }

    public void updateTileArtAround(int x, int y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                updateTileArtAt(i - 1 + x, j - 1 + y);
            }

        }
    }

    public void updateAreas(Random rand) {

        for (int i = 0; i < loadedAreas.length; i++) {
            for (int j = 0; j < loadedAreas[i].length; j++) {
                if (rand.nextBoolean()) {
                    loadedAreas[i][j].updateLayers(this, getAdjacentLoadedAreas(i, j), i + areaOffset[World.X], j + areaOffset[World.Y], rand);
                }
            }
        }
    }
}
