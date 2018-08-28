package com.tynellis.World.Entities;

import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Humanoid extends KillableEntity {
    protected boolean attacking = false;

    public Humanoid(int x, int y, int z, int width, int height) {
        super(x, y, z, width, height);
    }

    protected void meleeAttack(int facing, double breadth, DamageSource source, World world) {
        attacking = true;
        double attackDirection = (Math.PI / 4 * facing);
        double AttackXOffset = posX - (Math.sin(attackDirection) * (breadth / 2.0)) - ((breadth - 1) / 2.0);
        double AttackYOffset = posY - (Math.cos(attackDirection) * (breadth / 2.0)) - ((breadth - 1) / 2.0);
        Rectangle area = new Rectangle((int) ((AttackXOffset) * Tile.WIDTH), (int) ((AttackYOffset) * Tile.WIDTH), (int) (breadth * Tile.WIDTH), (int) (breadth * Tile.HEIGHT));

        ArrayList<Entity> hit = world.getEntitiesIntersecting(area);
        hit.remove(this);
        System.out.println("hit: " + hit);
        for (Entity e : hit) {
            if (e instanceof KillableEntity) {
                ((KillableEntity) e).DamageBy(source, world.getRand());
            }
        }
    }
}
