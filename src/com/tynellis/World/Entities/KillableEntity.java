package com.tynellis.World.Entities;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageModifiers.DamageConverter;
import com.tynellis.World.Entities.damage.DamageModifiers.DamageModifier;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.util.List;
import java.util.Random;

public abstract class KillableEntity extends Entity {

    private final int maxHealth = 20;
    private int health = maxHealth;
    protected DamageModifier resistance;
    protected DamageConverter damageConverter;

    protected boolean hurt = false;
    protected boolean heal = false;
    private int hurtCooldown = 0;
    private int healCooldown = 0;
    private int healthCooldownMax = 6;
    private int flashDuration = healthCooldownMax / 2;

    protected Container inventory;
    protected ItemPile holding;
    protected boolean canPickUpItems = false;

    public KillableEntity(double x, double y, double z, int width, int height) {
        super(x, y, z, width, height);
        inventory = new Container(0);
    }

    public ItemPile[] getItemsToDrop(Random rand) {
        return inventory.getContents();
    }

    public void tick(Region region, Random random, List<Entity> near) {
        super.tick(region, random, near);
        if (health <= 0) {
            kill();
        }
        inventory.tick();
    }

    public void DamageBy(DamageSource damage, Random rand) {
        if (hurtCooldown <= 0) {
            DamageSource converted;
            if (damageConverter != null) {
                converted = damageConverter.convertDamage(damage);
            } else {
                converted = damage;
            }
            for (Damage d : converted.dealDamage()) {
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

    public synchronized Container getInventory() {
        return inventory;
    }

    public void performDeath(Region region, Random random) {
        doDrops(region, random);
    }

    private void doDrops(Region region, Random random) {
        ItemPile[] items = getItemsToDrop(random);
        for (ItemPile pile : items) {
            if (pile != null && pile.getSize() > 0) {
                region.addEntity(new ItemEntity(pile, random, posX - 0.25 + (random.nextDouble() / 2), posY - 0.25 + (random.nextDouble() / 2), posZ));
            }
        }
    }
}
