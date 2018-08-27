package com.tynellis.World.Entities.damage;

import java.io.Serializable;
import java.util.HashMap;

public class DamageResistor implements Serializable {
    private HashMap<Damage.Types, Double> reductions;

    public DamageResistor(HashMap<Damage.Types, Double> resistance) {
        reductions = resistance;
    }

    public double reduceDamage(Damage damage) {
        if (reductions.containsKey(damage.type)) {
            return damage.amount * reductions.get(damage.type);
        } else {
            return damage.amount;
        }
    }
}
