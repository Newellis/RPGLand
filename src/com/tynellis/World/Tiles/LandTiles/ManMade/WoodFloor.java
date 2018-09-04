package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.LandTiles.Natural.Snow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class WoodFloor extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/mine/woodFloor.png", 32, 32, 1);
    private static final int RANK = 1;
    private static final double altPercent = 0.10;

    public WoodFloor(Random rand, int height) {
        super("Wood Floor", SHEET, rand, altPercent, RANK, height);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        //top = SHEET;
        startArt();
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Snow(rand, height);
    }
}
