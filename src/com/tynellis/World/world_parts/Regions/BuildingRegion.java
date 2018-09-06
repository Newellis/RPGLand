package com.tynellis.World.world_parts.Regions;

import com.tynellis.World.world_parts.Regions.Generator.WorldGen;

public class BuildingRegion extends Region {
    public BuildingRegion(String name, WorldGen gen) {
        super(name, gen);
    }

    @Override
    protected void addEntitiesToSpawn() {

    }
}
