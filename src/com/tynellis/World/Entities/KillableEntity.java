package com.tynellis.World.Entities;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageModifier;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public abstract class KillableEntity extends Entity {

    private int health = 20;
    protected DamageModifier resistance;
    private boolean hurt = false;
    private int hurtCooldown = 0;
    private int hurtCooldownMax = 10;

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
        if (hurtCooldown <= 0) {
            System.out.println("health: " + health);

            for (Damage d : damage.dealDamage()) {
                double amount;
                if (resistance != null) {
                    amount = resistance.modifyDamage(d);
                } else {
                    amount = d.amount;
                }
                if (amount > 0) {
                    hurt = true;
                    hurtCooldown = hurtCooldownMax;
                }
                System.out.println("health: " + health);
                health -= amount;
            }
        }
    }

    protected BufferedImage Tint(BufferedImage image, Color color) {
        if (hurt) {
            image.getRaster();
            BufferedImage tinted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = tinted.createGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();

            for (int i = 0; i < tinted.getWidth(); i++) {
                for (int j = 0; j < tinted.getHeight(); j++) {
                    int ax = tinted.getColorModel().getAlpha(tinted.getRaster().getDataElements(i, j, null));
                    int rx = tinted.getColorModel().getRed(tinted.getRaster().getDataElements(i, j, null));
                    int gx = tinted.getColorModel().getGreen(tinted.getRaster().getDataElements(i, j, null));
                    int bx = tinted.getColorModel().getBlue(tinted.getRaster().getDataElements(i, j, null));
                    rx = (color.getRed() + rx) / 2;
                    gx = (color.getGreen() + gx) / 2;
                    bx = (color.getBlue() + bx) / 2;
                    tinted.setRGB(i, j, (ax << 24) | (rx << 16) | (gx << 8) | (bx));
                }
            }

//            //Gray Scale Image
//            BufferedImageOp op = new ColorConvertOp(  ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//            op.filter(image, colored);
            return tinted;
        }
        return image;
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        if (hurtCooldown > 0) {
            hurtCooldown--;
        }
        if (hurtCooldown < hurtCooldownMax - 2) {
            hurt = false;
        }
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
