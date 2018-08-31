package com.tynellis.World.Entities.NPC.monsters;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toEntity.AttackEntityAi;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Items.weapons.Daggar;

import java.util.Random;

public class Skeleton extends NpcBase {
    public Skeleton(int x, int y, int z, Random random) {
        super("Skeleton", x, y, z, NpcGender.BOTH);
        equipWeapon(new Daggar("Pin Pricker", 25, 5, 3));
        Ai.addTask(1, new AttackEntityAi(Player.class, 10, 1));
    }

    @Override
    protected void setSprite(NpcGender gender) {
        spriteSheet = new SpriteSheet("tempArt/lpc/lpc_entry/png/walkcycle/BODY_skeleton.png", 64, 64, 1);
        attackSheet = new SpriteSheet("tempArt/lpc/lpc_entry/png/slash/BODY_skeleton.png", 64, 64, 1);
        animation = new Animation(spriteSheet, 5);
        animation.playInRange(spriteFacing, 1, 8);
        attackAnimation = new Animation(attackSheet, 2);
        attackAnimation.playFromStart(spriteFacing);
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }
}
