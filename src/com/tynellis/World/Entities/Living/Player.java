package com.tynellis.World.Entities.Living;

import com.tynellis.GameComponent;
import com.tynellis.GameState;
import com.tynellis.Menus.InGameMenus.InGameMenu;
import com.tynellis.Menus.InGameMenus.Inventory;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.UsableEntity.UsableEntity;
import com.tynellis.World.Entities.UsableEntity.using_interfaces.UsingInterface;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Tools.Hoe;
import com.tynellis.World.Light.LightSource;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;
import com.tynellis.debug.Debug;
import com.tynellis.input.Keys;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player extends LivingEntity {
    private transient Keys keys;
    private transient InGameMenu Inventory;

    public Player(Keys keys, String name, Gender gender, int x, int y, int z) {
        super(name, x, y, z, gender);
        this.keys = keys;
        speed = 0.08;
        movementType = movementTypes.Walking;
        inventory = new Container(20);
        canPickUpItems = true;
        light = new LightSource(12);
        equipWeapon(new Hoe("Chopper", 35, 5, 2));
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        System.out.println("load player " + hashCode() + " with " + keys);
    }

    @Override
    public ItemPile[] getItemsToDrop(Random rand) {
        ItemPile[] items = inventory.getContents();
        inventory = new Container(inventory.getContents().length);
        return items;
    }

    @Override
    public int compareTo(Entity entity) {
        if (entity instanceof Player) {
            return getName().compareTo(((Player) entity).getName());
        }
        return 0;
    }

    public void tick(Region region, Random random, List<Entity> near) {
        moving = false;
        if (keys != null) {
            if (keys.debug.wasPressed()) {
                GameComponent.debug.setState(!GameComponent.debug.State());
                if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.FLY)) {
                    movementType = movementTypes.Flying;
                } else if (!GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.FLY)) {
                    movementType = movementTypes.Walking;
                    posZ = region.getTopLayerAt((int) posX, (int) posY);
                }
            }
            if (keys.inventory.wasPressed()) {
                if (Inventory != null) {
                    Inventory.closeMenu();
                    Inventory = null;
                    GameComponent.active.setState(GameState.SINGLE_PLAYER);
                } else {
                    Inventory = new Inventory(this);
                    GameComponent.active.setMenu(Inventory);
                    GameComponent.active.setState(GameState.IN_GAME_MENU);
                }
            }
            if (Inventory == null) {//don't allow player to move use or attack while in the inventory screen
                if (keys.down.isDown && keys.right.isDown) {
                    facing = 5;
                    moving = true;
                } else if (keys.up.isDown && keys.right.isDown) {
                    facing = 7;
                    moving = true;
                } else if (keys.up.isDown && keys.left.isDown) {
                    facing = 1;
                    moving = true;
                } else if (keys.down.isDown && keys.left.isDown) {
                    facing = 3;
                    moving = true;
                } else if (keys.down.isDown) {
                    facing = 4;
                    moving = true;
                    spriteFacing = 2;
                } else if (keys.up.isDown) {
                    facing = 0;
                    moving = true;
                    spriteFacing = 0;
                } else if (keys.right.isDown) {
                    facing = 6;
                    moving = true;
                    //testPlayer.flipHoriz(true);
                } else if (keys.left.isDown) {
                    facing = 2;
                    moving = true;
                    //testPlayer.flipHoriz(false);
                }
                if (keys.right.isDown) {
                    spriteFacing = 3;
                } else if (keys.left.isDown) {
                    spriteFacing = 1;
                }
                if (keys.attack.wasPressed()) {
                    if (weapon.canUse(region, this)) {
                        meleeAttack(weapon, random, region);
                    }
                }
                if (keys.use.wasPressed()) {
                    UsableEntity usable = findUsableTarget(region);
                    if (usable != null && usable.canBeUsedBy(this)) {
                        UsingInterface usableInterface = usable.use(region, this);
                        if (usableInterface != null) {
                            Inventory = usableInterface.getMenu(this);
                            GameComponent.active.setMenu(Inventory);
                            GameComponent.active.setState(GameState.IN_GAME_MENU);
                        }
                    }
                }
            }
        }
        weapon.coolDownTick();
//        if (Region.DEBUG) {
//            speed = 0.32;
//        } else {
//            speed = 0.08;
//        }
        super.tick(region, random, near);
        light.setLocation(posX + 0.5, posY + 0.5, posZ);
    }

    private UsableEntity findUsableTarget(Region region) {
        double range = 1.5;

        double useDirection = (Math.PI / 4 * getFacing());
        double AttackXOffset = getX() - (Math.sin(useDirection) * (range / 2.0)) - ((range - 1) / 2.0);
        double AttackYOffset = getY() - (Math.cos(useDirection) * (range / 2.0)) - ((range - 1) / 2.0);

        Rectangle useArea = new Rectangle((int) ((AttackXOffset) * Tile.WIDTH), (int) ((AttackYOffset) * Tile.WIDTH) - (int) (3 * (getZ() / 4.0) * Tile.HEIGHT), (int) (range * Tile.WIDTH), (int) (range * Tile.HEIGHT));

        ArrayList<Entity> reach = region.getEntitiesIntersecting(useArea);
        double distance = Integer.MAX_VALUE;
        UsableEntity useable = null;
        for (Entity entity : reach) {
            if (entity instanceof UsableEntity) {
                double newDist = getDistanceTo(entity);
                if (newDist < distance) {
                    useable = (UsableEntity) entity;
                    distance = newDist;
                }
            }
        }
        return useable;
    }

    public void performDeath(Region region, Random random) {
        super.performDeath(region, random);
        respawn(region, random);
    }

    private void respawn(Region region, Random random) {
        isDead = false;
        int x, y, z;
        do {
            x = GameComponent.world.getSpawnPoint()[0] + (random.nextInt(20) - 10);
            y = GameComponent.world.getSpawnPoint()[1] + (random.nextInt(20) - 10);
            z = region.getTopLayerAt(x, y);
        } while (region.isTileObstructed(x, y, z) && !region.getTile(x, y, z).isPassableBy(this));

        setLocation(x, y, z);
        Heal(20);
        region.addEntity(this);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return e.isFlying();
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return movementType == movementTypes.Flying;
    }

    public void setKeys(Keys keys) {
        System.out.println("Set Keys " + keys);
        this.keys = keys;
    }

    public void removeMenu() {
        Inventory = null;
    }
}
