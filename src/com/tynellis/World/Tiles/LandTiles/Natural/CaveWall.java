package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;

import java.awt.*;
import java.util.Random;

public class CaveWall extends Tile {

    private static final double altPercent = 0.10;
    private static SpriteSheet caveSheet = new SpriteSheet("tempArt/lpc/core/tiles/mountains.png", 32, 64, 0, 64, 1);
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/mine/cliffWall.png", 32, 32, 1);
    protected boolean wall;

    public CaveWall(Random rand, int height) {
        super("Cave_Wall", SHEET, rand, altPercent, TileRank.Wall, height);
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    @Override
    public void render(Graphics g, int x, int y) {
        super.render(g, x, y);
        if (wall) {
            g.drawImage(caveSheet.getSprite(0).getStill(1), x, y - Tile.HEIGHT, null);
        }
    }

    @Override
    public void updateArt(Tile[][] adjacent) {
        wall = !(adjacent[1][2] instanceof CaveWall);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return true;
    }

    @Override
    public boolean isPassableBy(Entity.movementTypes movementType) {
        return true;
    }

    @Override
    public Tile newTile(Random rand, int height) {
        return new CaveWall(rand, height);
    }

    @Override
    public double getTraversalDifficulty(Entity e) {
        return 0;
    }
}
