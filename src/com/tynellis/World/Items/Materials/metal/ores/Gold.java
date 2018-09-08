package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.GoldNug;
import com.tynellis.World.Items.Smeltable;

public class Gold extends Smeltable {
    public Gold() {
        super("Gold Ore", 10, 300, 10, 0, 4);
    }

    @Override
    protected Item getCooked() {
        return new GoldNug();
    }
}
