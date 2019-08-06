package com.tynellis.World.Entities.NPC.animals;

import com.tynellis.World.Entities.NPC.NpcBase;

public abstract class Animal extends NpcBase {


    protected Animal(String name, int x, int y, int z, NpcGender gender) {
        super(name, x, y, z, gender);
    }
}
