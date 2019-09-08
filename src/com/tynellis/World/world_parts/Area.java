package com.tynellis.World.world_parts;

import com.tynellis.GameComponent;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;
import com.tynellis.debug.Debug;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

public class Area implements Land, Serializable {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int DEPTH = 6;
    private Layer[] layers = new Layer[DEPTH];
    private boolean updateArt = true, populate = true;

    public Area(Random rand) {
        for (int d = 0; d < DEPTH; d++) {
            layers[d] = new Layer(rand);
        }
    }

    public void update(Random rand) {

    }

    public void renderStrip(int row, int height, Graphics g, int xOffset, int yOffset) {
        layers[height].renderRow(row, g, xOffset, yOffset - (int) (3 * (height / 4.0) * Tile.HEIGHT));
    }

    public void renderRow(int row, Graphics g, int xOffset, int yOffset) {
        for (int i = 0; i < layers.length; i++) {
            if (layers[i] != null) {
                layers[i].renderRow(row, g, xOffset, yOffset - (int) (3 * (i / 4.0) * Tile.HEIGHT));
                if (i - 1 >= 0 && layers[i - 1] != null) {
                    layers[i - 1].renderRowTop(row, g, xOffset, yOffset - (int) (3 * ((i - 1) / 4.0) * Tile.HEIGHT));
                }
            }
        }
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        for (int i = 0; i < Area.HEIGHT; i++) {
            renderRow(i, g, xOffset, yOffset);
        }
//        for (int i = 0; i < layers.length; i++) {
//            if (layers[i] != null) {
//                layers[i].render(g, xOffset, yOffset - (int) (3 * (i / 4.0) * Tile.HEIGHT));
//            }
//            if (i-1 >= 0 && layers[i-1] != null) {
//                layers[i-1].renderTop(g, xOffset, yOffset - (int) (3 * ((i - 1) / 4.0) * Tile.HEIGHT));
//            }
//        }
        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.AREAS)) {
            g.setColor(Color.RED);
            Rectangle bounds = getBounds();
            g.drawRect(xOffset, yOffset, bounds.width, bounds.height);
        }

    }

    public void updateLayerArt(Area[][] areas) {
        if (updateArt) {
            for (int i = 0; i < layers.length; i++) {
                Layer[][] adjacent = new Layer[areas.length][areas[0].length];
                for (int j = 0; j < areas.length; j++) {
                    for (int k = 0; k < areas[j].length; k++) {
                        if (areas[j][k] != null) {
                            adjacent[j][k] = areas[j][k].getLayer(i);
                        }
                    }
                }
                layers[i].updateTileArt(adjacent);
            }
            updateArt = false;
        }
    }

    public void updateLayers(Region region, Area[][] areas, int X, int Y, Random rand) {
        for (int i = 0; i < layers.length; i++) {
            if (rand.nextBoolean()) {
                Layer[][] adjacent = new Layer[areas.length][areas[0].length];
                for (int j = 0; j < areas.length; j++) {
                    for (int k = 0; k < areas[j].length; k++) {
                        if (areas[j][k] != null) {
                            adjacent[j][k] = areas[j][k].getLayer(i);
                        }
                    }
                }
                layers[i].updateTiles(region, adjacent, X, Y, i, rand);
            }
        }
    }

    private Layer getLayer(int i) {
        return layers[i];
    }

    public static Rectangle getBounds(){
        return new Rectangle(WIDTH * Tile.WIDTH, HEIGHT * Tile.HEIGHT);
    }

    public Tile getTile(int X, int Y, int Z) {
        return layers[Z].getTile(X, Y);
    }

    public void setTile(Tile tile, int x, int y, int  z){
        layers[z].setTile(tile, x, y);

    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        updateArt = true;
    }

    public boolean shouldPopulate(){
        return populate;
    }

    public boolean shouldUpdateArt() {
        return updateArt;
    }

    public void shouldUpdateArt(boolean should) {
        updateArt = should;
    }


    public void Populate(){
        populate = false;
    }
}
