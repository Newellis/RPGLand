package com.tynellis.World.Entities.NPC.villagers;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.NPC.NpcBase;

import java.util.Random;

public class Villager extends NpcBase {
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
