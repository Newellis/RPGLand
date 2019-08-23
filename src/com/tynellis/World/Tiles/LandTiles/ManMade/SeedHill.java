package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.Sprite;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Items.Seed;
import com.tynellis.World.Tiles.LandTiles.LayeredTile;
import com.tynellis.World.Tiles.Tile;

import java.awt.*;
import java.util.Random;

public class SeedHill extends LayeredTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/mine/plowed_soil.png", 32, 32, 1);
    private static final double altPercent = 0.35;

    public SeedHill(Seed item, Random rand, Tile base) {
        super(item.getCrop().getName() + " Mound", null, rand, altPercent, base.getRank(), base.getHeightInWorld(), base);
    }

    @Override
    protected void setSprite() {
        top = null;
    }

    @Override
    public void render(Graphics g, int x, int y) {
        getBase().render(g, x, y);
    }

    @Override
    public void renderTop(Graphics g, int x, int y) {
        Sprite image = SHEET.getSprite(1);
        g.drawImage(image.getStill(0), x, y, null);

    }

    @Override
    public Tile newTile(Random rand, int height) {
        return null;
    }
}
