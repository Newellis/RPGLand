package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.GameComponent;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.ItemEntity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.UsableEntity.using_interfaces.UsingInterface;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.Cookable;
import com.tynellis.World.Items.Item;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Items.Materials.Stone;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class FirePit extends UsableEntity {
    private boolean built = false;
    private boolean onFire = false;
    private boolean redHot = false;
    private transient Animation fireAnim;
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/hearth.png", 32, 64, 1);
    private DamageSource damageSource;
    private int burnTime = 0;
    private int heat = 0;

    public FirePit(double x, double y, double z) {
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
                heat = burnTime / 300;
            }
            redHot = heat != 0;
            if (built) {
                onFire = true;
            }
        }
        if (heat > 10) {
            heat = 10;
        }
        if (heat > 0 && redHot) {
            CookItems(random);
        }
        super.tick(region, random, near);
    }

    private void CookItems(Random random) {
        for (ItemPile pile : inventory.getContents()) {
            if (pile != null && pile.getItem() instanceof Cookable) {
                Item result = ((Cookable) pile.getItem()).CookTick(heat, random);
                if (result != null) {
                    pile.replaceStack(new ItemPile(result, pile.getSize()));
                }
            }
        }
    }

    @Override
    public UsingInterface use(Region region, KillableEntity entity) {
        if (built) {
            onFire = !onFire;
            if (onFire) {
                fireAnim = new Animation(SHEET, 5);
                fireAnim.playInRange(0, 2, 4);
            }
        } else if (!redHot) {
            for (int i = 1; i < inventory.getContents().length; i++) {
                if (inventory.getContents()[i] != null) {
                    System.out.print("Drop: " + inventory.getContents()[i].getItem().getName());
                    //todo figure out how to get items back to user without loss
                    region.queueAdditionOfEntity(new ItemEntity(inventory.getContents()[i], GameComponent.world.getRand(), (getX() + entity.getX()) / 2, (getY() + entity.getY()) / 2, region.getTopLayerAt((int) ((getX() + entity.getX()) / 2), (int) ((getY() + entity.getY()) / 2))));
                }
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

        if (bb instanceof ItemPile && onFire) {
            ((ItemPile) bb).removeFromPile(((ItemPile) bb).getSize());
        }
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
