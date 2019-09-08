package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.Tile;

import java.util.Random;

public class TilledSoil extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/mine/plowed_soil.png", 32, 32, 1);
    private static final double altPercent = 0;

    public TilledSoil(Random rand, int height) {
        super("Tilled_Soil", SHEET, rand, altPercent, TileRank.TilledSoil, height);
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    @Override
    public Tile newTile(Random rand, int height) {
        return new TilledSoil(rand, height);
    }

    @Override
    public double getTraversalDifficulty(Entity e) {
        if (e.isWalking()) {
            return 1.0;
        } else if (e.isFlying()) {
            return 1.0;
        }
        return 0;
    }
}
