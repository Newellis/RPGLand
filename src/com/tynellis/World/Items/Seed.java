package com.tynellis.World.Items;

import com.tynellis.World.Entities.Plants.Crops.Crop;
import com.tynellis.World.Items.Tools.UsableItem;

import java.util.Random;

public abstract class Seed extends UsableItem {
    private Crop crop;
    private int growLength;

    public Seed(Crop crop, int growLength, int artRow, int artCol) {
        super(crop.getName() + " Seed", 100, artRow, artCol);
        this.crop = crop;
        this.growLength = growLength;
    }

    public Crop getCrop() {
        return crop;
    }

    public Crop getNewCrop(Random rand, double x, double y, double z) {
        return crop.newCrop(rand, x, y, z);
    }

    public int GrowDuration() {
        return growLength;
    }
}

