package com.tynellis.World.Tiles;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.Entities.Entity;

import java.util.Random;

public abstract class LiquidTile extends Tile{
    public LiquidTile(String name, SpriteSheet sheet, Random rand, double altPercent, int rank) {
        super(name, sheet, rand, altPercent, rank);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return !e.isWalking() && !isObstructed();
    }
    public boolean isPassableBy(Entity.movementTypes movementType){
        return movementType != Entity.movementTypes.Walking;
    }
}
