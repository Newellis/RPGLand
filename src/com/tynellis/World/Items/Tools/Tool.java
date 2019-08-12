package com.tynellis.World.Items.Tools;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Tools.Weapons.Weapon;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public abstract class Tool extends Weapon {
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

    public Point2D getAttackPoint(KillableEntity user) {
        double attackDirection = (Math.PI / 4 * user.getFacing());
        double AttackXOffset = (user.getX() + (user.getWidth() / 2.0 / Tile.WIDTH)) - (Math.sin(attackDirection) * getRange());
        double AttackYOffset = (user.getY() + (user.getHeight() / 2.0 / Tile.HEIGHT)) - (Math.cos(attackDirection) * getRange());
        return new Point2D.Double(AttackXOffset, AttackYOffset);
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

            if (hit.size() <= 0) { //todo change to use button not attack
                boolean used = useOnTile(region, random, user);
                coolDownTimer = coolDown;
                return used;
            }

            coolDownTimer = coolDown;
            return true;
        }
        return false;
    }

    private boolean useOnTile(Region region, Random random, KillableEntity user) {
        Point2D point = getAttackPoint(user);
        double x, y, z;
        x = point.getX();
        y = point.getY();
        z = Math.round(user.getZ());
        System.out.println("get tile at " + x + ", " + y + ", " + z);
        System.out.println("user at " + user.getX() + ", " + user.getY() + ", " + user.getZ());
        Tile tile = region.getTile((int) x, (int) y, (int) z);
        return useOnTile(region, tile, x, y, z, random);
    }

    protected abstract boolean useOnTile(Region region, Tile tile, double x, double y, double z, Random random);
}
