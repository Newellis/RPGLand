package com.tynellis.World.Entities;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageResistor;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.World;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public abstract class KillableEntity extends Entity {

    private int health = 20;
    private DamageResistor resistance;
    private boolean hurt = false;

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

    public void DamageBy(DamageSource damage) {
        System.out.println("health: " + health);

        for (Damage d : damage.dealDamage()) {
            double amount;
            if (resistance != null) {
                amount = resistance.reduceDamage(d);
            } else {
                amount = d.amount;
            }
            if (amount > 0) {
                hurt = true;
            }
            System.out.println("health: " + health);
            health -= amount;
        }
    }

    protected BufferedImage HurtTint(BufferedImage image) {
        if (hurt) {
        }
        return image;
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        hurt = false;
        super.render(g, xOffset, yOffset);
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
