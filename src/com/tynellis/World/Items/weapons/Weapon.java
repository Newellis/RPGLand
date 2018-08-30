package com.tynellis.World.Items.weapons;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.UsableItem;
import com.tynellis.World.World;

import java.awt.Rectangle;

public abstract class Weapon extends UsableItem {
    private double range;
    protected DamageSource damage;
    protected int coolDown, coolDownTimer;

    public Weapon(String name, double range, int coolDown, DamageSource damage, int artCol, int artRow) {
        super(name, 1, artCol, artRow);
        this.damage = damage;
        this.coolDown = coolDown;
        this.range = range;
    }

    public boolean canUse(World world, KillableEntity user) {
        return coolDownTimer <= 0;
    }

    public double getRange() {
        return range;
    }

    public abstract Rectangle getAttackArea(KillableEntity attacker);

    public DamageSource getAttackDamage() {
        return damage;
    }

    public void coolDownTick() {
        if (coolDownTimer > 0) {
            coolDownTimer--;
        }
    }
}
