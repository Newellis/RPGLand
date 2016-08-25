package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.Tile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class Dirt extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/dirt2.png", 32, 32, 1);
    private static final int RANK = 3;
    private static final double altPercent = 0.35;

    public Dirt(Random rand, int height) {
        super("Dirt", SHEET, rand, altPercent, RANK, height);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        top = SHEET;
        startArt();
    }

    @Override
    public void update(Tile[][] adjacent) {
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Dirt(rand, height);
    }
}
