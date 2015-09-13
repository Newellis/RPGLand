package com.tynellis.Entities;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.input.Keys;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Player extends Mob {
    private transient Keys keys;
    private transient SpriteSheet spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
    private transient Animation animation = new Animation(spriteSheet, 5);
    private String name;

    public Player(Keys keys, String name, int x, int y, int z) {
        super(x, y, z, 32 ,32);
        this.keys = keys;
        animation.playInRange(spriteFacing, 1, 8);
        this.name = name;
        speed = 0.08;
        movementType = movementTypes.Walking;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
        animation = new Animation(spriteSheet, 5);
        animation.playInRange(spriteFacing, 1, 8);
    }
    public void tick(World world) {
        moving = false;
        if (keys.debug.wasPressed()) {
            World.DEBUG = !World.DEBUG;
            if (movementType == movementTypes.Walking) {
                movementType = movementTypes.Flying;
            } else movementType = movementTypes.Walking;
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
//        if (World.DEBUG) {
//            speed = 0.32;
//        } else {
//            speed = 0.08;
//        }
        super.tick(world);
        animation.setRow(spriteFacing);
    }

    public void render(Graphics g, int xOffset, int yOffset) {

        BufferedImage frame;
        if (!moving) {
            animation.pause();
            animation.skipToFrame(0);
        } else {
            animation.play();
        }
        frame = animation.getFrame();
        g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (height * 1.5)), null);
        animation.tick();
        if (World.DEBUG) {
            g.setColor(Color.WHITE);
            g.drawString("X,Y,Z: " + posX + ", " + posY + ", " + posZ, 10, 34);
        }
        super.render(g, xOffset, yOffset);
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

    public void setKeys(Keys keys){
        this.keys = keys;
    }
}
