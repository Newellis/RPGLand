package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;

import java.awt.*;
import java.util.Random;

public class Wall extends Tile {
    private static final double altPercent = 0.10;
    private static SpriteSheet wallSheet = new SpriteSheet("tempArt/lpc/buildings/cottage.png", 32, 64, 1);
    private int wallType;
    private boolean wall;

    public Wall(Random rand, int height, int wallType) {
        super("Wall", cliffEdge, rand, altPercent, TileRank.Wall, height);
        this.wallType = wallType;
    }

    @Override
    protected void setSprite() {
        top = cliffEdge;
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
    public double getTraversalDifficulty(Entity e) {
        return 0;
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
