package com.tynellis.World.Items.Materials.metal.ores;

import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.Materials.metal.Nuggets.TinNug;
import com.tynellis.World.Items.Smeltable;

public class Tin extends Smeltable {
    public Tin() {
        super("Tin Ore", 4, 200, 10, 0, 3);
    }

    @Override
    protected Item getCooked() {
        return new TinNug();
    }
}
