package com.tynellis.World.Tiles;

import com.tynellis.Art.SpriteSheet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class Water extends LiquidTile{
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/water.png", 32, 32, 1);

    private static final double altPercent = 0.025;

    public Water(Random rand, int height) {
        super("Water", SHEET, rand, altPercent, height);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        top = SHEET;
        startArt();
    }

    public SpriteSheet getSheet(Tile tile){
        return top;
    }

    @Override
    public Tile newTile(Random rand, int height) {
        return new Water(rand, height);
    }
}
