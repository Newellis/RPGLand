package com.tynellis.World.world_parts.Regions;

import com.tynellis.World.world_parts.Regions.Generator.IWorldGen;

public class BuildingRegion extends Region {
    public BuildingRegion(String name, IWorldGen gen) {
        super(name, gen);
    }

    @Override
    protected void addEntitiesToSpawn() {

    }
}
