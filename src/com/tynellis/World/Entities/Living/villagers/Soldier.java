package com.tynellis.World.Entities.Living.villagers;

import com.tynellis.World.Entities.Living.Ai.Pathfinding.toEntity.AttackEntityAi;
import com.tynellis.World.Entities.Living.monsters.Skeleton;
import com.tynellis.World.Items.Tools.Weapons.Sword;

import java.util.Random;

public class Soldier extends Villager {
    protected Soldier(String name, int x, int y, int z, Gender gender) {
        super(name, x, y, z, gender);
        equipWeapon(new Sword(getName(Gender.MALE, new Random()) + " The Slayer", 25, 5, 3));
        Ai.addTask(1, new AttackEntityAi(Skeleton.class, 20, 1));
    }

    public Soldier(int x, int y, int z, Gender gender, Random random) {
        this(getName(gender, random), x, y, z, gender);
        setLooking(random.nextInt(4));
    }

    public Soldier(int x, int y, int z, Random random) {
        this(x, y, z, Gender.values()[random.nextInt(Gender.values().length)], random);
    }
}
