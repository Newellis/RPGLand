package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.LandTiles.Natural.Snow;

import java.util.Random;

public class WoodFloor extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/mine/woodFloor.png", 32, 32, 1);
    private static final double altPercent = 0.10;

    public WoodFloor(Random rand, int height) {
        super("Wood Floor", SHEET, rand, altPercent, TileRank.WoodFloor, height);
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Snow(rand, height);
    }
}
