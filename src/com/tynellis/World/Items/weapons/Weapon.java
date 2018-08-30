package com.tynellis.World.Items.weapons;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.UsableItem;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Weapon extends UsableItem {
    private double range;
    protected DamageSource damage;
    protected int coolDown, coolDownTimer;
    protected transient SpriteSheet weaponSheet = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/character/sword_sheet_128.png", 128, 126, 1);
    protected transient Animation weaponAnimation = new Animation(weaponSheet, 2);


    public Weapon(String name, double range, int coolDown, DamageSource damage, int artCol, int artRow) {
        super(name, 1, artCol, artRow);
        this.damage = damage;
        this.coolDown = coolDown;
        this.range = range;
    }

    public boolean canUse(World world, KillableEntity user) {
        if (coolDownTimer <= 0) {
            weaponAnimation.playFromStart(user.getSpriteFacing());
            return true;
        }
        return false;
    }

    public double getRange() {
        return range;
    }

    public abstract Rectangle getAttackArea(KillableEntity attacker);

    public DamageSource getAttackDamage() {
        return damage;
    }

    public void renderAttack(Graphics g, int xOffset, int yOffset, KillableEntity user) {
        BufferedImage frame;
        weaponAnimation.setRow(user.getSpriteFacing());
        weaponAnimation.play();
        System.out.println("attacking with " + getName() + " frame " + weaponAnimation.getFrameNum());
        frame = weaponAnimation.getFrame();
        g.drawImage(frame, (int) ((user.getX() + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((user.getY() + 0.5) * Tile.HEIGHT) + yOffset - (user.getHeight() * 2.5)) - (int) (3 * (user.getZ() / 4.0) * Tile.HEIGHT), null);
        weaponAnimation.tick();
    }

    public void coolDownTick() {
        if (coolDownTimer > 0) {
            coolDownTimer--;
        }
    }
}
