package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.Tile;

import java.util.Random;

public class Sand extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/tilesets/sand.png", 32, 32, 1);
    //private static final SpriteSheet WATER = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/tilesets/sandwater.png", 32, 32, 1);
    private static final int RANK = 3;
    private static final double altPercent = 0.10;


    public Sand(Random rand, int height) {
        super("Sand", SHEET, rand, altPercent, RANK, height);
    }

    public SpriteSheet getSheet(Tile tile) {
//        if (tile != null && tile.getName().compareTo("Water") == 0) {
//            return WATER;
//        }
        return top;
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    @Override
    public void update(Tile[][] adjacent) {
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Sand(rand, height);
    }
}
