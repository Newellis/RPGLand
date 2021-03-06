package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LandTile;

import java.util.Random;

public class Dirt extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/dirt2.png", 32, 32, 1);
    private static final double altPercent = 0.35;

    public Dirt(Random rand, int height) {
        super("Dirt", SHEET, rand, altPercent, TileRank.Dirt, height);
    }

    //for dirt variations ex.Grass
    public Dirt(String name, SpriteSheet sheet, Random rand, double altPercent, TileRank rank, int height) {
        super(name, sheet, rand, altPercent, rank, height);
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Dirt(rand, height);
    }
}
