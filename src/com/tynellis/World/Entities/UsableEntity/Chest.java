package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Chest extends UsableEntity {
    public static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/chests.png", 32, 32, 1);
    private transient ArrayList<ChestInterface> interfaces = new ArrayList<ChestInterface>();
    private boolean isOpen = false;

    public Chest(double x, double y, double z) {
        super(x, y, z, 32, 32);
        speed = 0.0;
        canBeMoved = false;
        inventory = new Container(40);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        interfaces = new ArrayList<ChestInterface>();
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        BufferedImage image = SHEET.getSprite(0).getStill(0);
        if (isOpen) {
            image = SHEET.getSprite(1).getStill(0);
        }
        g.drawImage(image, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (image.getWidth() / 2), (int) (((posY) * Tile.HEIGHT) + yOffset) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        super.render(g, xOffset, yOffset);
        if (interfaces.size() < 1) {
            isOpen = false;
        }
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return e.isFlying();
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return movementType == movementTypes.Flying;
    }

    protected void addItem(ItemPile pile) {
        inventory.addItemPile(pile);
    }

    public String toString() {
        return "Chest{" + getClass().getName() +
                ", posY= " + posY +
                ", posX= " + posX +
                ", posZ= " + posZ +
                ", Inventory= " + getInventory() +
                '}';
    }

    @Override
    public Object use(KillableEntity entity) {
        if (canUse(entity)) {
            ChestInterface chestInterface = new ChestInterface(this);
            interfaces.add(chestInterface);
            isOpen = true;
            return chestInterface;
        }
        return null;
    }

    public void removeInterface(ChestInterface chestInterface) {
        interfaces.remove(chestInterface);
    }

    public boolean canAddItem(ItemPile pile) {
        return inventory.canAddItem(pile);
    }

    public boolean isFull() {
        return inventory.isFull();
    }
}
