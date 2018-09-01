package com.tynellis.World.Entities;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageModifier;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.World;

import java.awt.Graphics;
import java.util.List;
import java.util.Random;

public abstract class KillableEntity extends Entity {

    private final int maxHealth = 20;
    private int health = maxHealth;
    protected DamageModifier resistance;

    protected boolean hurt = false;
    protected boolean heal = false;
    private int hurtCooldown = 0;
    private int healCooldown = 0;
    private int healthCooldownMax = 6;
    private int flashDuration = healthCooldownMax / 2;

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

    public void DamageBy(DamageSource damage, Random rand) {
        if (hurtCooldown <= 0) {

            for (Damage d : damage.dealDamage()) {
                double amount;
                if (resistance != null) {
                    amount = resistance.modifyDamage(d);
                } else {
                    amount = d.amount;
                }
                if (amount > 0) {
                    hurt = true;
                    hurtCooldown = healthCooldownMax;
                }
                if (amount < 0) {
                    heal = true;
                    healCooldown = healthCooldownMax;
                }

                //adjust damage fractions to percent of taking 1 damage
                if (!((Double) amount).equals(Math.floor(amount))) {
                    if (rand.nextDouble() <= amount % 1.0) {
                        amount++;
                    }
                }
                health -= (int) amount;
                if (health > maxHealth) {
                    health = maxHealth;
                }
            }
        }
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        if (hurtCooldown > 0) {
            hurtCooldown--;
        }
        if (hurtCooldown < healthCooldownMax - flashDuration) {
            hurt = false;
        }
        if (healCooldown > 0) {
            healCooldown--;
        }
        if (healCooldown < healthCooldownMax - flashDuration) {
            heal = false;
        }
        super.render(g, xOffset, yOffset);
    }

    public void Heal(int amount) {
        if (amount >= maxHealth || health + amount >= maxHealth) {
            health = maxHealth;
        } else {
            health += amount;
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

    public void performDeath(World world) {
        doDrops(world);
    }

    private void doDrops(World world) {
        ItemPile[] items = getItemsToDrop(world.getRand());
        for (ItemPile pile : items) {
            if (pile != null && pile.getSize() > 0) {
                world.addEntity(new ItemEntity(pile, world.getRand(), posX - 0.25 + (world.getRand().nextDouble() / 2), posY - 0.25 + (world.getRand().nextDouble() / 2), posZ));
            }
        }
    }
}
