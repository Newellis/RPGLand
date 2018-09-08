package com.tynellis.World.Items.weapons;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class Tool extends Weapon {
    public Tool(String name, double range, int coolDown, int artCol, int artRow) {
        super(name, range, coolDown, new DamageSource(), artCol, artRow);
    }

    @Override
    public Rectangle getAttackArea(KillableEntity user) {
        double attackDirection = (Math.PI / 4 * user.getFacing());
        double AttackXOffset = user.getX() - (Math.sin(attackDirection) * (getRange() / 2.0)) - ((getRange() - 1) / 2.0);
        double AttackYOffset = user.getY() - (Math.cos(attackDirection) * (getRange() / 2.0)) - ((getRange() - 1) / 2.0);

        return new Rectangle((int) ((AttackXOffset) * Tile.WIDTH), (int) ((AttackYOffset) * Tile.WIDTH) - (int) (3 * (user.getZ() / 4.0) * Tile.HEIGHT), (int) (getRange() * Tile.WIDTH), (int) (getRange() * Tile.HEIGHT));
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
}
