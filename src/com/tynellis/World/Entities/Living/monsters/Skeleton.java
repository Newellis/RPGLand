package com.tynellis.World.Entities.Living.monsters;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.toEntity.AttackEntityAi;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Items.Tools.Weapons.Daggar;

import java.util.Random;

public class Skeleton extends LivingEntity {
    public Skeleton(int x, int y, int z, Random random) {
        super("Skeleton", x, y, z, NpcGender.BOTH);
        equipWeapon(new Daggar("Pin Pricker", 25, 5, 3));
        Ai.addTask(1, new AttackEntityAi(Player.class, 10, 1));
        setLooking(random.nextInt(4));
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
