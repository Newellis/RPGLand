package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.GameComponent;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.UsableEntity.using_interfaces.UsingInterface;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Items.Materials.Stone;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class Hearth extends UsableEntity {
    private boolean built = false;
    private boolean onFire = false;
    private boolean redHot = false;
    private transient Animation fireAnim;
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/hearth.png", 32, 64, 1);
    private DamageSource damageSource;
    private int burnTime = 0;
    private int heat = 0;

    public Hearth(double x, double y, double z) {
        super(x, y, z, 32, 32);
        speed = 0.0;
        canBeMoved = false;
        inventory = new Container(4);
        inventory.addItemPile(new ItemPile(new Stone(), 10));
        canPickUpItems = true;
        damageSource = new DamageSource(new Damage(Damage.Types.FIRE, 3));
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        BufferedImage image = SHEET.getSprite(0).getStill(0);
        if (onFire) {
            image = fireAnim.getFrame();
            fireAnim.tick();
        } else if (redHot) {
            image = SHEET.getSprite(0).getStill(4);
        } else if (built) {
            image = SHEET.getSprite(0).getStill(1);
        }
        g.drawImage(image, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (image.getWidth() / 2), (int) (((posY) * Tile.HEIGHT) + yOffset - (image.getHeight() - height)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        super.render(g, xOffset, yOffset);
    }

    public void tick(Region region, Random random, List<Entity> near) {
        if (!built) {
            int logsOnFire = 0;
            for (ItemPile item : inventory.getContents()) {
                if (item != null && item.getItem() instanceof Log) {
                    logsOnFire += item.getSize();
                }
            }
            if (logsOnFire >= 3) {
                built = true;
            }
        }
        if (onFire) {
            built = false;
            redHot = true;
            burnTime++;
            if (burnTime % 300 >= 299) {
                ItemPile useItem = null;
                for (ItemPile item : inventory.getContents()) {
                    if (item != null && item.getItem() instanceof Log) {
                        useItem = new ItemPile(item.getItem(), 1);
                        inventory.consumeItem(useItem);
                        heat++;
                        break;
                    }
                }
                if (useItem == null) {
                    built = false;
                    onFire = false;
                }
            }
        } else if (redHot) {
            if (heat > 0) {
                burnTime--;
                heat = burnTime % 300;
            }
            redHot = heat != 0;
        }
        if (heat > 10) {
            heat = 10;
        }
        super.tick(region, random, near);
    }

    @Override
    public UsingInterface use(KillableEntity entity) {
        if (built) {
            onFire = !onFire;
            if (onFire) {
                fireAnim = new Animation(SHEET, 5);
                fireAnim.playInRange(0, 2, 4);
            }
        }
        return null;
    }

    public void DamageBy(DamageSource damage, Random rand) {
        for (Damage d : damage.dealDamage()) {
            if (d.type == Damage.Types.FIRE && !onFire && built) {
                onFire = true;
            }
            if (d.type == Damage.Types.FREEZING && redHot && d.amount > heat) {
                onFire = false;
                heat = 0;
            }
            if (d.type == Damage.Types.PIERCING && redHot && rand.nextDouble() < 0.25) {
                onFire = true;
            }
        }
        super.DamageBy(damage, rand);
    }

    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove, boolean isOver) {
        if (redHot && bb instanceof KillableEntity) {
            ((KillableEntity) bb).DamageBy(damageSource, GameComponent.world.getRand());
        }
        super.handleCollision(bb, xMove, yMove, isOver);
    }

    @Override
    public int compareTo(Entity entity) {
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
