package com.tynellis.World.Items.weapons;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Region;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Sword extends Weapon {

    public Sword(String name, int coolDown, int artCol, int artRow) {
        super(name, 1.5, coolDown, new DamageSource(new Damage(Damage.Types.SLICING, 5)), artCol, artRow);
        weaponSheet = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/character/sword_sheet_128.png", 128, 126, 1);
        weaponAnimation = new Animation(weaponSheet, 2);
    }

    @Override
    public boolean use(Region region, Random random, KillableEntity user) {
        if (canUse(region, user)) {
            Rectangle area = getAttackArea(user);
            ArrayList<Entity> hit = region.getEntitiesIntersecting(area);
            hit.remove(user);
            for (Entity e : hit) {
                if (e instanceof KillableEntity) {
                    ((KillableEntity) e).DamageBy(getAttackDamage(), random);
                }
            }
            coolDownTimer = coolDown;
            return true;
        }
        return false;
    }

    @Override
    public Rectangle getAttackArea(KillableEntity user) {
        double attackDirection = (Math.PI / 4 * user.getFacing());
        double AttackXOffset = user.getX() - (Math.sin(attackDirection) * (getRange() / 2.0)) - ((getRange() - 1) / 2.0);
        double AttackYOffset = user.getY() - (Math.cos(attackDirection) * (getRange() / 2.0)) - ((getRange() - 1) / 2.0);

        return new Rectangle((int) ((AttackXOffset) * Tile.WIDTH), (int) ((AttackYOffset) * Tile.WIDTH) - (int) (3 * (user.getZ() / 4.0) * Tile.HEIGHT), (int) (getRange() * Tile.WIDTH), (int) (getRange() * Tile.HEIGHT));
    }
}
