package com.tynellis.World.Tiles.LandTiles;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;

import java.util.Random;


public abstract class ConnectorTile extends LayeredTile {

    protected int direction;
    protected double height, bottom;

    public ConnectorTile(String name, SpriteSheet sheet, Random rand, double altPercent, int rank, int height, Tile base) {
        super(name, sheet, rand, altPercent, rank, height, base);
    }

    public int getDirection() {
        return direction;
    }

    public abstract boolean canUse(Entity e);

    public double getHeight() {
        return (bottom + height) / 2.0;
    }
}
