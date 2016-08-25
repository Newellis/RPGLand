package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.Tile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class LavaRock extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/lavarock.png", 32, 32, 1);
    private static final int RANK = 4;
    private static final double altPercent = 0.15;

    public LavaRock(Random rand, int height) {
        super("Lava Rock", SHEET, rand, altPercent, RANK, height);
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
    public Tile newTile(Random rand, int height) {
        return new LavaRock(rand, height);
    }
}
