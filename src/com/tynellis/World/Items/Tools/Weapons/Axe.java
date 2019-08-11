package com.tynellis.World.Items.Tools.Weapons;

import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Items.Tools.Tool;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class Axe extends Tool {

    public Axe(String name, int coolDown, int artCol, int artRow) {
        super(name, 1, coolDown, artCol, artRow);

        damage.addDamageType(new Damage(Damage.Types.SLICING, 3));
        damage.addDamageType(new Damage(Damage.Types.BLUNT, 3));
    }

    @Override
    protected boolean useOnTile(Region region, Tile tile, double x, double y, double z, Random random) {
        return false;
    }
}
