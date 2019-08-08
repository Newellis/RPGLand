package com.tynellis.World.Entities.Living.animals;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.Core.PathfindInRangeAi;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.toTile.EatTileAi;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.toTile.RandomWanderAi;
import com.tynellis.World.Tiles.LandTiles.Natural.Dirt;
import com.tynellis.World.Tiles.LandTiles.Natural.Grass;

import java.util.Random;

public class Rabbit extends Animal {
    public Rabbit(int x, int y, int z, Random random) {
        super("Rabbit", x, y, z, random);
//        Ai.addTask(0, new StupidRunAwayAi(AttackingEntity.class, 0.3));
        Ai.addTask(1, new EatTileAi(Grass.class, new Dirt(new Random(), 0), 20));
        Ai.addTask(2, new RandomWanderAi(15, 60));
        //Ai.addTask(1, new FollowEntityAi(Player.class, 60, 4));
        setLooking(random.nextInt(4));

        width = 20;
        height = 20;
        speed = 0.07;
        pathfinder = new PathfindInRangeAi(100, x, y, z);
    }

    @Override
    protected void setSprite(NpcGender gender) {
        spriteSheet = new SpriteSheet("tempArt/lpc/mine/bunnysheet_reform.png", 35, 35, 1);
        attackSheet = new SpriteSheet("tempArt/lpc/mine/bunnysheet_reform.png", 35, 35, 1);
        animation = new Animation(spriteSheet, 5);
        animation.playInRange(spriteFacing, 1, 4);
        attackAnimation = new Animation(attackSheet, 2);
        attackAnimation.playInRange(spriteFacing, 5, 8);
        attackAnimation.playFromStart(spriteFacing);
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }
}
