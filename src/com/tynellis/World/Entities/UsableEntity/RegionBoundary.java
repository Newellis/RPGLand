package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.GameComponent;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.world_parts.Regions.Region;

public abstract class RegionBoundary extends UsableEntity {
    private Region region;

    public RegionBoundary(double x, double y, double z, int width, int height, Region region) {
        super(x, y, z, width, height);
        this.region = region;
        speed = 0.0;
        canBeMoved = false;
    }

    @Override
    public UsingInterface use(KillableEntity entity) {
        if (canBeUsedBy(entity)) {
            System.out.println(entity.getClass().getSimpleName() + " using door");
            GameComponent.world.moveEntityToRegion(entity, region);
            return null;
        }
        return null;
    }

    public Region getDestination() {
        return region;
    }
}