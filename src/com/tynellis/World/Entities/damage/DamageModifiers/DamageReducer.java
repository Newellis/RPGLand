package com.tynellis.World.Entities.damage.DamageModifiers;

import com.tynellis.World.Entities.damage.Damage;

import java.util.HashMap;

public class DamageReducer extends DamageModifier {
    public DamageReducer(HashMap<Damage.Types, Double> resistance) {
        super(resistance);
    }

    public double modifyDamage(Damage damage) {
        if (modifiers.containsKey(damage.type)) {
            return (damage.amount - modifiers.get(damage.type) >= 1) ? damage.amount - modifiers.get(damage.type) : 1;
        } else {
            return damage.amount;
        }
    }
}
