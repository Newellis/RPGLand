package com.tynellis.World.Entities;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Region;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Random;

public class ItemEntity extends Entity {
    private ItemPile item;
    private transient double floatOffset = 0, floatChange = -0.25;
    private int age = 0;

    public ItemEntity(ItemPile item, Random rand, double x, double y, double z) {
        super(x, y, z, 32, 32);
        this.item = item;
        floatOffset = rand.nextInt(item.getItem().getImage().getHeight() / 4);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.floatOffset = 0;
        this.floatChange = -0.25;
    }

    public void tick(Region region, Random random, List<Entity> near) {
        if (item.getSize() <= 0 || age == 60 * 60 * 5) {
            kill();
        } else {
            super.tick(region, random, near);
            Rectangle rect = getBounds();
            double[] centerPoint = isOverPoint();
            for (Entity entity : near) {
                if (rect.intersects(entity.getBounds()) && posZ == entity.getZ() && entity instanceof ItemEntity) {
                    handleCollision(entity, 0, 0, entity.getBounds().contains(centerPoint[0], centerPoint[1]));
                }
            }
            age++;
        }
        super.tick(region, random, near);
    }

    public ItemPile getItem() {
        return item;
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        BufferedImage image = item.getItem().getImage();
        if (floatOffset > image.getHeight() / 4) {
            floatChange = -floatChange;
        } else if (floatOffset <= 0) {
            floatChange = Math.abs(floatChange);
        }
        floatOffset += floatChange;
        if (item.getSize() >= item.getItem().getMaxStackSize() / 2) {
            g.drawImage(image, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (image.getWidth() / 2) - (image.getWidth() / 6), (int) (((posY) * Tile.HEIGHT) + yOffset) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT + floatOffset) - (2 * image.getHeight() / 15), null);
            g.drawImage(image, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (image.getWidth() / 2) + (image.getWidth() / 6), (int) (((posY) * Tile.HEIGHT) + yOffset) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT + floatOffset), null);
            if (item.getSize() >= item.getItem().getMaxStackSize()) {
                g.drawImage(image, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (image.getWidth() / 2), (int) (((posY) * Tile.HEIGHT) + yOffset) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT + floatOffset) - (image.getHeight() / 3), null);
            }
        } else {
            g.drawImage(image, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (image.getWidth() / 2), (int) (((posY) * Tile.HEIGHT) + yOffset) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT + floatOffset), null);
        }
        super.render(g, xOffset, yOffset);
    }

    @Override
    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove, boolean isOver) {
        if (bb instanceof ItemEntity) {
            if (age <= ((ItemEntity) bb).age && item.canAddPile(((ItemEntity) bb).getItem())) {
                item.addPile(((ItemEntity) bb).getItem());
                if (((ItemEntity) bb).getItem().getSize() <= 0) {
                    ((ItemEntity) bb).kill();
                }
            }
        }
        //todo push out of immovable entities hitboxes
    }

    @Override
    public void performDeath(Region region, Random random) {

    }

    @Override
    public int compareTo(Entity entity) {
        if (entity instanceof ItemEntity) {
            if (item.getSize() > ((ItemEntity) entity).item.getSize()) {
                return 1;
            } else if (item.getSize() > ((ItemEntity) entity).item.getSize()) {
                return -1;
            } else {
                int alphabetical = item.getItem().getName().compareTo(((ItemEntity) entity).getItem().getItem().getName());
                if (alphabetical == 0) {
                    return age - ((ItemEntity) entity).age;
                }
                return alphabetical;
            }
        }
        return 0;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return true;
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return true;
    }
}
