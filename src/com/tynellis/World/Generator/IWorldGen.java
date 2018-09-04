package com.tynellis.World.Generator;

import com.tynellis.World.world_parts.Regions.Region;

public interface IWorldGen {
    void fillArea(Region region, int x, int y, long seed);

    void styleArea(Region region, int x, int y, long seed);

    void populateArea(Region region, int x, int y, long seed);
}
