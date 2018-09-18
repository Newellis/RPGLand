package com.tynellis.World.Items.Materials.metal.Nuggets;

import com.tynellis.World.Items.Item;

public class MetalNugget extends Item {
    protected double purity;

    public MetalNugget(String name, double purity, int artRow, int artCol) {
        super(name, 20, artRow, artCol);
        this.purity = purity;
    }
}
