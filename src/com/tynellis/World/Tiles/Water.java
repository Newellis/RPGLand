package com.tynellis.World.Tiles;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;

import java.util.Random;

public class Water extends LiquidTile{
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/water.png", 32, 32, 1);

    private static final double altPercent = 0.025;

    public Water(Random rand, int height) {
        super("Water", SHEET, rand, altPercent, height);
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    public SpriteSheet getSheet(Tile tile){
        return top;
    }

    @Override
    public Tile newTile(Random rand, int height) {
        return new Water(rand, height);
    }

    @Override
    public double getTraversalDifficulty(Entity e) {
        if (e.isSwimming()) {
            return 1.0;
        } else if (e.isFlying()) {
            return 1.0;
        }
        return 0;
    }
}
