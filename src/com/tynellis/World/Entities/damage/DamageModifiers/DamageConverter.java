package com.tynellis.World.Entities.damage.DamageModifiers;

import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;

import java.io.Serializable;
import java.util.HashMap;

public class DamageConverter implements Serializable {
    protected HashMap<Damage.Types, Damage.Types> modifiers;

    public DamageConverter(HashMap<Damage.Types, Damage.Types> modifiers) {
        this.modifiers = modifiers;
    }

    public DamageSource convertDamage(DamageSource damage) {
        DamageSource modified = new DamageSource();
        for (Damage d : damage.dealDamage()) {
            if (modifiers.containsKey(d.type)) {
                modified.addDamageType(new Damage(modifiers.get(d.type), d.amount));
            } else {
                modified.addDamageType(d);
            }
        }
        return modified;
    }
}
