package com.tynellis.World.Entities.damage.DamageModifiers;

import com.tynellis.World.Entities.damage.Damage;

import java.io.Serializable;
import java.util.HashMap;

public class DamageModifier implements Serializable {
    protected HashMap<Damage.Types, Double> modifiers; //damage modifier by percent 2.0 = 1x heal amount, 1.0 = zero damage, -1.0 = 2x damage amount

    public DamageModifier(HashMap<Damage.Types, Double> resistance) {
        modifiers = resistance;
    }

    public double modifyDamage(Damage damage) {
        if (modifiers.containsKey(damage.type)) {
            return damage.amount * (1 - modifiers.get(damage.type));
        } else {
            return damage.amount;
        }
    }
}
