package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.NPC.monsters.Skeleton;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.World.spawners.Spawner;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Chest extends UsableEntity {
    public static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/chests.png", 32, 32, 1);
    private transient ArrayList<ChestInterface> interfaces = new ArrayList<ChestInterface>();
    private boolean isOpen = false;

    private Spawner spawner;
    private Rectangle spawnerArea;

    public Chest(double x, double y, double z) {
        super(x, y, z, 32, 32);
        speed = 0.0;
        canBeMoved = false;
        inventory = new Container(40);

        spawnerArea = new Rectangle(((int) x - 5) * Tile.WIDTH, ((int) y - 5) * Tile.WIDTH, 11 * Tile.WIDTH, 11 * Tile.WIDTH);
        spawner = new Spawner(50, spawnerArea, Skeleton.class, 5);
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
        g.setColor(Color.GRAY);
        g.drawRect(spawnerArea.x + xOffset, spawnerArea.y + yOffset, spawnerArea.width, spawnerArea.height);
        super.render(g, xOffset, yOffset);
        if (interfaces.size() < 1) {
            isOpen = false;
        }
    }

    @Override
    public void tick(World world, List<Entity> near) {
        super.tick(world, near);
        spawner.tick(world);
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
