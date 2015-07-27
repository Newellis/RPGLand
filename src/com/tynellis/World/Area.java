package com.tynellis.World;

import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Tile;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

public class Area implements Land, Serializable {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int DEPTH = 2;
    private Layer[] layers = new Layer[DEPTH];
    private boolean updateArt = true, populate = true;

    public Area(Random rand) {
        for (int d = 0; d < DEPTH; d++) {
            layers[d] = new Layer(rand);
        }
    }

    public void update(Random rand) {

    }

    public void render(Graphics g, int topLayer, int xOffset, int yOffset) {
        if (layers[topLayer] != null) {
            layers[topLayer].render(g, xOffset, yOffset - (int) (2 * (topLayer / 3.0) * Tile.HEIGHT));
        }
        if (World.DEBUG) {
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

    public void Populate(){
        populate = false;
    }
}
