package com.tynellis.World.Tiles;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.Entities.Entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class Water extends LiquidTile{
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/water.png", 32, 32, 1);
    private static final int RANK = 0;
    private static final double altPercent = 0.025;

    public Water(Random rand) {
        super("Water", SHEET, rand, altPercent, RANK);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        top = SHEET;
        startArt();
    }

    public SpriteSheet getSheet(Tile tile){
        return top;
    }
}
