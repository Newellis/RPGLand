package com.tynellis.World.world_parts.Regions;

import com.tynellis.World.World;
import com.tynellis.World.world_parts.Regions.Generator.CaveGen;

public class CaveRegion extends Region {
    public CaveRegion(World world, int depth) {
        super("Cave_" + depth, new CaveGen(world, depth));
    }

    @Override
    protected void addEntitiesToSpawn() {
        //worldSpawner.addEntitySpawn(Skeleton.class, 50);
    }
}
