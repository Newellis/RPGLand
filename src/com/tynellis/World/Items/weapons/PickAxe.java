package com.tynellis.World.Items.weapons;

import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;

public class PickAxe extends Sword {
    public PickAxe(String name, int coolDown, int artCol, int artRow) {
        super(name, coolDown, artCol, artRow);

        damage = new DamageSource();
        damage.addDamageType(new Damage(Damage.Types.PIERCING, 3));
        damage.addDamageType(new Damage(Damage.Types.BLUNT, 3));
    }
}
