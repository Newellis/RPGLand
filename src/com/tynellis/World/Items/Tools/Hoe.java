package com.tynellis.World.Items.Tools;

import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Tiles.LandTiles.ManMade.TilledSoil;
import com.tynellis.World.Tiles.LandTiles.Natural.Dirt;
import com.tynellis.World.Tiles.LandTiles.Natural.Grass;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class Hoe extends Tool {

    public Hoe(String name, int coolDown, int artCol, int artRow) {
        super(name, 1, coolDown, artCol, artRow);
        damage.addDamageType(new Damage(Damage.Types.BLUNT, 3));
    }

    @Override
    protected boolean useOnTile(Region region, Tile tile, double x, double y, double z, Random random) {
        System.out.println("use hoe on " + tile.getName());
        x -= 0.5;
        y -= 0.5;
        if ((int) Math.floor(x) == (int) Math.ceil(x)) x -= 0.1;
        if ((int) Math.floor(y) == (int) Math.ceil(y)) y -= 0.1;
        for (int X = (int) Math.floor(x); X <= (int) Math.ceil(x); X++) {
            for (int Y = (int) Math.floor(y); Y <= (int) Math.ceil(y); Y++) {
                Tile test = region.getTile(X, Y, (int) z);
                if (test instanceof Grass || test instanceof Dirt) {
                    region.setTile(new TilledSoil(random, test.getHeightInWorld()), X, Y, (int) z);
                    region.updateTileArtAround(X, Y);
                }
            }
        }
        return false;
    }
}
