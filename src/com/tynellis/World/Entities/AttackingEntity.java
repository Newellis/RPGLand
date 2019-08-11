package com.tynellis.World.Entities;

import com.tynellis.World.Items.Tools.Weapons.Weapon;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public abstract class AttackingEntity extends KillableEntity {
    protected boolean attacking = false;

    public AttackingEntity(int x, int y, int z, int width, int height) {
        super(x, y, z, width, height);
    }

    protected void meleeAttack(Weapon weapon, Random random, Region region) {
        attacking = true;
        weapon.use(region, random, this);
    }
}
