package com.tynellis.Save;

import com.tynellis.World.world_parts.Regions.Region;

import java.io.Serializable;

public class SavedRegion implements Serializable {
    private Region region;

    public SavedRegion(Region region) {
        this.region = region;
    }

    public Region getRegion() {
        return region;
    }
}
