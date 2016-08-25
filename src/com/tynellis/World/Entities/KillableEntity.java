package com.tynellis.World.Entities;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.World;

import java.util.List;
import java.util.Random;

public abstract class KillableEntity extends Entity {

    private int health = 20;
    protected Container inventory;
    protected boolean canPickUpItems = false;

    public KillableEntity(double x, double y, double z, int width, int height) {
        super(x, y, z, width, height);
        inventory = new Container(0);
    }

    public ItemPile[] getItemsToDrop(Random rand) {
        return inventory.getContents();
    }

    public void tick(World world, List<Entity> near) {
        super.tick(world, near);
        if (health <= 0) {
            kill();
        }
    }

    private void pickUpItem(ItemPile pile) {
        if (canPickUpItems) {
            inventory.addItemPile(pile);
        }
    }

    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove, boolean isOver) {
        super.handleCollision(bb, xMove, yMove, isOver);
        if (bb instanceof ItemEntity && canPickUpItems && !((ItemEntity) bb).isDead() && inventory.canAddItem(((ItemEntity) bb).getItem())) {
            pickUpItem(((ItemEntity) bb).getItem());
            if (((ItemEntity) bb).getItem().getSize() <= 0) {
                ((ItemEntity) bb).kill();
            }
        }
    }

    public Container getInventory() {
        return inventory;
    }
}
