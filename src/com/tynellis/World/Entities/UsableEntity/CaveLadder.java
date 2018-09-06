package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.Art.Sprite;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class CaveLadder extends RegionBoundry {
    private static transient SpriteSheet ladderSheet = new SpriteSheet("tempArt/lpc/mine/CaveLadder.png", 32, 64, 1);
    private boolean top;

    public CaveLadder(double x, double y, double z, boolean top, Region region) {
        super(x, y, z, 32, (top) ? 32 : 4, region);
        this.top = top;
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        Sprite doorSprite = ladderSheet.getSprite(0);
        BufferedImage door = doorSprite.getStill(top ? 1 : 0);
        g.drawImage(door, (int) (posX * Tile.WIDTH) + xOffset, (int) (((posY) * Tile.HEIGHT) + yOffset) - Tile.HEIGHT - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        super.render(g, xOffset, yOffset);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return false;
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return false;
    }
}
