package com.tynellis.World.Entities.damage.DamageModifiers;

import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DamageCombinations extends DamageConverter {
    public DamageCombinations(HashMap<Damage.Types, Damage.Types> modifiers) {
        super(modifiers);
    }

    public DamageSource convertDamage(DamageSource damage) {
        DamageSource modified = new DamageSource();
        List<Damage> used = new ArrayList<Damage>();
        for (Damage d : damage.dealDamage()) {
            if (modifiers.containsKey(d.type)) {
                for (Damage da : damage.dealDamage()) {
                    if (da.type == modifiers.get(d.type)) {
                        double damageValue = d.amount;
                        if (da.amount < damageValue) damageValue = da.amount;
                        modified.addDamageType(new Damage(d.type, damageValue));
                        used.add(da);
                    }
                }
            } else if (!used.contains(d)) {
                modified.addDamageType(d);
            }
        }
        return modified;
    }
}
