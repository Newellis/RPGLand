package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.Art.Sprite;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Door extends UsableEntity {
    private static transient SpriteSheet doorSheet = new SpriteSheet("tempArt/lpc/buildings/doors.png", 64, 64, 1);

    private Region region;

    private boolean locked = false;
    private boolean open = false;

    public Door(double x, double y, double z, int width, Region region) {
        super(x, y, z, width * Tile.WIDTH, 4);
        speed = 0.0;
        canBeMoved = false;
        this.region = region;
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        Sprite doorSprite = doorSheet.getSprite(4);
        BufferedImage door = doorSprite.getStill(0);
        g.drawImage(door, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (door.getWidth() / 2) - 1, (int) (((posY) * Tile.HEIGHT) + yOffset) - Tile.HEIGHT - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        super.render(g, xOffset, yOffset);
    }

    @Override
    public UsingInterface use(KillableEntity entity) {
        if (canBeUsedBy(entity)) {
            System.out.println(entity.getClass().getSimpleName() + " using door");
            Open(!open);
            World.moveEntityToRegion(entity, region);
            return null;
        }
        return null;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) ((posX + 0.5) * Tile.WIDTH) - (width / 2), (int) ((posY + 0.5) * Tile.HEIGHT) - height - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), width, height);
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return false;
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return false;
    }

    public void setLocked(boolean lock) {
        locked = lock;
    }

    public boolean Open(boolean opened) {
        if (!locked) {
            open = opened;
        }
        return !locked;
    }
}
