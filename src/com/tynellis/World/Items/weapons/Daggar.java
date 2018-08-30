package com.tynellis.World.Items.weapons;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Daggar extends Sword {

    public Daggar(String name, int coolDown, int artCol, int artRow) {
        super(name, coolDown, artCol, artRow);
        damage = new DamageSource(new Damage(Damage.Types.SLICING, .5));
        weaponSheet = new SpriteSheet("tempArt/lpc/lpc_entry/png/slash/WEAPON_dagger.png", 64, 64, 1);
        weaponAnimation = new Animation(weaponSheet, 2);
    }

    public void renderAttack(Graphics g, int xOffset, int yOffset, KillableEntity user) {
        BufferedImage frame;
        weaponAnimation.setRow(user.getSpriteFacing());
        weaponAnimation.play();
        frame = weaponAnimation.getFrame();
        g.drawImage(frame, (int) ((user.getX() + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((user.getY() + 0.5) * Tile.HEIGHT) + yOffset - (user.getHeight() * 1.5)) - (int) (3 * (user.getZ() / 4.0) * Tile.HEIGHT), null);
        weaponAnimation.tick();
    }
}
