package com.tynellis.World.Entities.damage;


import java.util.ArrayList;
import java.util.List;

public class DamageSource {
    private List<Damage> damageTypes;

    public DamageSource(List<Damage> DamageTypes) {
        this.damageTypes = DamageTypes;
    }

    public DamageSource(Damage damage) {
        this();
        damageTypes.add(damage);
    }

    public DamageSource() {
        this(new ArrayList<Damage>());
    }

    public void addDamageType(Damage damage) {
        damageTypes.add(damage);
    }

    public List<Damage> dealDamage() {
        return damageTypes;
    }
}
