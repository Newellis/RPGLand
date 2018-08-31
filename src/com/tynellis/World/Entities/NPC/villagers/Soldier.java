package com.tynellis.World.Entities.NPC.villagers;

import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity.AttackEntityAi;
import com.tynellis.World.Entities.NPC.monsters.Skeleton;
import com.tynellis.World.Items.weapons.Sword;

import java.util.Random;

public class Soldier extends Villager {
    protected Soldier(String name, int x, int y, int z, NpcGender gender) {
        super(name, x, y, z, gender);
        equipWeapon(new Sword(getName(NpcGender.MALE, new Random()) + " The Slayer", 25, 5, 3));
        Ai.addTask(1, new AttackEntityAi(Skeleton.class, 20, 1));
    }

    public Soldier(int x, int y, int z, NpcGender gender, Random random) {
        this(getName(gender, random), x, y, z, gender);
        setLooking(random.nextInt(4));
    }

    public Soldier(int x, int y, int z, Random random) {
        this(x, y, z, NpcGender.values()[random.nextInt(NpcGender.values().length)], random);
    }
}
