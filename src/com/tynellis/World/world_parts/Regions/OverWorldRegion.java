package com.tynellis.World.world_parts.Regions;

import com.tynellis.World.world_parts.Regions.Generator.WorldGen;

public class OverWorldRegion extends Region {
    public OverWorldRegion(String name, WorldGen gen) {
        super(name, gen);
    }

    @Override
    protected void addEntitiesToSpawn() {
//        worldSpawner.addEntitySpawn(Skeleton.class, 100);
    }
}
