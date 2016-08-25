package com.tynellis.World.Tiles.LandTiles;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;

import java.util.Random;

public abstract class LandTile extends Tile {
    public LandTile(String name, SpriteSheet sheet, Random rand, double altPercent, int rank, int height) {
        super(name, sheet, rand, altPercent, rank, height);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return !e.isSwimming();
    }

    public boolean isPassableBy(Entity.movementTypes movementType) {
        return movementType != Entity.movementTypes.Swimming;
    }
}
