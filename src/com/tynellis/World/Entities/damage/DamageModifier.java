package com.tynellis.World.Entities.damage;

import java.io.Serializable;
import java.util.HashMap;

public class DamageModifier implements Serializable {
    private HashMap<Damage.Types, Double> modifiers; // damage modifier by percent 1.0 = full reduction, -1.0 = double damage

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
