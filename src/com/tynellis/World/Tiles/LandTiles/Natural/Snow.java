package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LandTile;

import java.util.Random;

public class Snow extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/mine/snow.png", 32, 32, 1);
    private static final int RANK = 1;
    private static final double altPercent = 0.10;

    public Snow(Random rand, int height) {
        super("Snow", SHEET, rand, altPercent, RANK, height);
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
