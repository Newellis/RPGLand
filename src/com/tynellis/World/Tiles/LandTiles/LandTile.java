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
    public double getTraversalDifficulty(Entity e) {
        if (e.isWalking()) {
            return 1.0;
        } else if (e.isFlying()) {
            return 1.0;
        }
        return 0;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return !e.isSwimming();
    }

    public boolean isPassableBy(Entity.movementTypes movementType) {
        return movementType != Entity.movementTypes.Swimming;
    }
}
