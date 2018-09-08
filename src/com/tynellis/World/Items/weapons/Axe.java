package com.tynellis.World.Items.weapons;

import com.tynellis.World.Entities.damage.Damage;

public class Axe extends Tool {

    public Axe(String name, int coolDown, int artCol, int artRow) {
        super(name, 1, coolDown, artCol, artRow);

        damage.addDamageType(new Damage(Damage.Types.SLICING, 3));
        damage.addDamageType(new Damage(Damage.Types.BLUNT, 3));
    }
}
