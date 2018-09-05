package com.tynellis.World.world_parts.Regions;

import com.tynellis.World.Entities.NPC.monsters.Skeleton;
import com.tynellis.World.world_parts.Regions.Generator.IWorldGen;

public class OverWorldRegion extends Region {
    public OverWorldRegion(String name, IWorldGen gen) {
        super(name, gen);
    }

    @Override
    protected void addEntitiesToSpawn() {
        worldSpawner.addEntitySpawn(Skeleton.class, 100);
    }
}
