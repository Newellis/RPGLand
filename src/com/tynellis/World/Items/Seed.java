package com.tynellis.World.Items;

import com.tynellis.World.Entities.Plants.Crops.Crop;
import com.tynellis.World.Items.Tools.UsableItem;

public abstract class Seed extends UsableItem {
    private Crop crop;

    public Seed(Crop crop, int artRow, int artCol) {
        super(crop.getName() + " Seed", 100, artRow, artCol);
    }

    public Crop getCrop() {
        return crop;
    }
}

