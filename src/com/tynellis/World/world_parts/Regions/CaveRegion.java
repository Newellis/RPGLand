package com.tynellis.World.world_parts.Regions;

import com.tynellis.World.World;
import com.tynellis.World.world_parts.Regions.Generator.CaveGen;

public class CaveRegion extends Region {
    public CaveRegion(String name, World world) {
        super(name, new CaveGen(world));
    }

    @Override
    protected void addEntitiesToSpawn() {

    }
}
