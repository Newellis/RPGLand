package com.tynellis.World.Entities;

import com.tynellis.World.Items.weapons.Weapon;
import com.tynellis.World.World;

public abstract class Humanoid extends KillableEntity {
    protected boolean attacking = false;

    public Humanoid(int x, int y, int z, int width, int height) {
        super(x, y, z, width, height);
    }

    protected void meleeAttack(Weapon weapon, World world) {
        attacking = true;
        weapon.use(world, this);
    }
}
