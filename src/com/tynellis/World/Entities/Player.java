package com.tynellis.World.Entities;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteImage;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.GameComponent;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.Containers.Filters.ItemFilter;
import com.tynellis.World.Items.Containers.Filters.NameItemFilter;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.weapons.Sword;
import com.tynellis.World.Items.weapons.Weapon;
import com.tynellis.World.Light.LightSource;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.debug.Debug;
import com.tynellis.input.Keys;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Random;

public class Player extends Humanoid {
    private transient Keys keys;
    private transient SpriteSheet spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
    private transient Animation animation = new Animation(spriteSheet, 5);
    private transient SpriteSheet attackSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_slash.png", 64, 64, 1);
    private transient Animation attackAnimation = new Animation(attackSheet, 2);
    private transient SpriteSheet swordSheet = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/character/sword_sheet_128.png", 128, 126, 1);
    private transient Animation swordAnimation = new Animation(swordSheet, 2);
    private String name;

    private Weapon weapon = new Sword("Awesome Sauce", 6, 5, 1);

    public Player(Keys keys, String name, int x, int y, int z) {
        super(x, y, z, 32, 32);
        this.keys = keys;
        animation.playInRange(spriteFacing, 1, 8);
        attackAnimation.playFromStart(spriteFacing);
        swordAnimation.playFromStart(spriteFacing);
        this.name = name;
        speed = 0.08;
        movementType = movementTypes.Walking;
        inventory = new Container(20);
        inventory.setFilter(new NameItemFilter(new String[]{"Acorn"}, ItemFilter.Type.WhiteList));
        canPickUpItems = false;
        light = new LightSource(12);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
        animation = new Animation(spriteSheet, 5);
        attackSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_slash.png", 64, 64, 1);
        attackAnimation = new Animation(attackSheet, 2);
        swordSheet = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/character/sword_sheet_128.png", 128, 126, 1);
        swordAnimation = new Animation(swordSheet, 2);
        animation.playInRange(spriteFacing, 1, 8);
        attackAnimation.playFromStart(spriteFacing);
        swordAnimation.playFromStart(spriteFacing);
        attacking = false;
    }

    @Override
    public ItemPile[] getItemsToDrop(Random rand) {
        return new ItemPile[0];
    }

    @Override
    public int compareTo(Entity entity) {
        if (entity instanceof Player) {
            return name.compareTo(((Player) entity).getName());
        }
        return 0;
    }

    public void tick(World world, List<Entity> near) {
        moving = false;
        if (keys.debug.wasPressed()) {
            GameComponent.debug.setState(!GameComponent.debug.State());
            if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.FLY)) {
                movementType = movementTypes.Flying;
            } else if (!GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.FLY)) {
                movementType = movementTypes.Walking;
                posZ = world.getTopLayerAt((int) posX, (int) posY);
            }
        }
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
            if (weapon.canUse(world, this)) {
                meleeAttack(weapon, world);
            }
        }
        weapon.coolDownTick();
//        if (World.DEBUG) {
//            speed = 0.32;
//        } else {
//            speed = 0.08;
//        }
        super.tick(world, near);
        animation.setRow(spriteFacing);
        attackAnimation.setRow(spriteFacing);
        swordAnimation.setRow(spriteFacing);
        light.setLocation(posX + 0.5, posY + 0.5, posZ);
    }

    public void render(Graphics g, int xOffset, int yOffset) {

        BufferedImage frame;
        if (!moving) {
            animation.pause();
            animation.skipToFrame(0);
        } else {
            animation.play();
        }
        if (!attacking) {
            frame = animation.getFrame();
            if (hurt) {
                frame = SpriteImage.Tint(frame, Damage.BLEED_COLOR);
            }
            g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (height * 1.5)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
            animation.tick();
        }

        if (!attacking) {
            attackAnimation.pause();
            attackAnimation.skipToFrame(0);
            swordAnimation.pause();
            swordAnimation.skipToFrame(0);
        } else {
            swordAnimation.play();
            attackAnimation.play();
            if (attackAnimation.getFrameNum() == 5) {
                attacking = false;
            }
            frame = attackAnimation.getFrame();
            if (hurt) {
                frame = SpriteImage.Tint(frame, Damage.BLEED_COLOR);
            }
            g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (height * 1.5)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
            attackAnimation.tick();
            frame = swordAnimation.getFrame();
            g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (height * 2.5)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
            swordAnimation.tick();
        }
        if (GameComponent.debug.State()) {
            g.setColor(Color.WHITE);
            g.drawString("X,Y,Z: " + posX + ", " + posY + ", " + posZ, 10, 34);
            if (GameComponent.debug.isType(Debug.Type.ATTACK)) {
                Rectangle rectangle = weapon.getAttackArea(this);
                g.setColor(Color.RED);
                g.drawRect(rectangle.x + xOffset, rectangle.y + yOffset, rectangle.width, rectangle.height);
            }
        }

        super.render(g, xOffset, yOffset);
    }

    public void performDeath(World world) {
        super.performDeath(world);
        isDead = false;
        setLocation(world.getSpawnPoint()[0], world.getSpawnPoint()[1], world.getSpawnPoint()[2]);
        Heal(20);
        world.addEntity(this);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return e.isFlying();
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return movementType == movementTypes.Flying;
    }

    public String getName() {
        return name;
    }

    public void setKeys(Keys keys) {
        this.keys = keys;
    }
}
