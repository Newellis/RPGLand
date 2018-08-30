package com.tynellis.World.Items.weapons;

import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;

public class Daggar extends Sword {

    public Daggar(String name, int coolDown, int artCol, int artRow) {
        super(name, coolDown, artCol, artRow);
        damage = new DamageSource(new Damage(Damage.Types.SLICING, .5));
    }
}
