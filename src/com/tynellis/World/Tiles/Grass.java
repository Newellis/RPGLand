package com.tynellis.World.Tiles;
import com.tynellis.Art.SpriteSheet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class Grass extends LandTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/grass.png", 32, 32, 1);
    private static final SpriteSheet ALT = new SpriteSheet("tempArt/lpc/core/tiles/terain/grassalt.png", 32, 32, 1);
    //private static final SpriteSheet WATER = new SpriteSheet("tempArt/lpc/mine/grasswater.png", 32, 32, 1);
    private static final int RANK = 1;
    private static final double altPercent = 0.10;


    public Grass(Random rand) {
        super("Grass", SHEET, rand, altPercent, RANK);
    }

    public SpriteSheet getSheet(Tile tile){
        if (tile != null && tile.getName().compareTo("Water") == 0) {
            return ALT;
        }
        return top;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        top = SHEET;
        startArt();
    }

    @Override
    public void update() {
    }
}
