package com.tynellis.World.Entities.Living.villagers;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Entities.Living.Types.Person;

import java.util.Random;

public class Villager extends LivingEntity implements Person {
    protected Villager(String name, int x, int y, int z, NpcGender gender) {
        super(name, x, y, z, gender);
    }

    public Villager(int x, int y, int z, NpcGender gender, Random random) {
        super(x, y, z, gender, random);
    }

    public Villager(int x, int y, int z, Random random) {
        super(x, y, z, random);
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }
}
