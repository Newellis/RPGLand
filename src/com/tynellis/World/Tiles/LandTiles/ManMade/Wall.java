package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class Wall extends Tile {
    private static final int RANK = 1;
    private static final double altPercent = 0.10;
    private static SpriteSheet wallSheet = new SpriteSheet("tempArt/lpc/buildings/cottage.png", 32, 64, 1);
    private int wallType;
    private boolean wall;

    public Wall(Random rand, int height, int wallType) {
        super("Wall", cliffEdge, rand, altPercent, RANK, height);
        this.wallType = wallType;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        //top = SHEET;
        startArt();
    }

    @Override
    public void render(Graphics g, int x, int y) {
        if (wall) {
            g.drawImage(wallSheet.getSprite(wallType * 2).getStill(1), x, y - Tile.HEIGHT, null);
        }
    }

    @Override
    public void updateArt(Tile[][] adjacent) {
        wall = !(adjacent[1][2] instanceof Wall);
    }

    @Override
    public Tile newTile(Random rand, int height) {
        return new Wall(rand, height, wallType);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return false;
    }

    @Override
    public boolean isPassableBy(Entity.movementTypes movementType) {
        return false;
    }
}
