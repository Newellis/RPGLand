package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LayeredTile;
import com.tynellis.World.Tiles.Tile;

import java.util.Random;

public class SeedHill extends LayeredTile {

    public SeedHill(String name, SpriteSheet sheet, Random rand, double altPercent, int rank, int height, Tile base) {
        super(name, sheet, rand, altPercent, rank, height, base);
    }

    @Override
    protected void setSprite() {

    }

    @Override
    public Tile newTile(Random rand, int height) {
        return null;
    }
}
