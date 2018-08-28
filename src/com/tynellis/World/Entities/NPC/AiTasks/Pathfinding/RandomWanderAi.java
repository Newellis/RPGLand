package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.World;

import java.util.Random;

public class RandomWanderAi extends PathfinderAi {
    private Random random;
    private boolean newDes = true;
    private int timer = 0;

    public RandomWanderAi() {
        super(100, 0);
        random = new Random();
    }


    @Override
    public boolean performTask(World world, NpcBase entity) {
        if (newDes || timer <= 0) {
            path.clear();
            double x = Math.round(entity.getX()) + (10 - random.nextInt(21)), y = Math.round(entity.getY()) + 0.5 + (10 - random.nextInt(21));
            while (world.getTile((int) x, (int) y, world.getTopLayerAt((int) x, (int) y)) == null || world.getEntitiesIntersecting(world.getTileBounds((int) x, (int) y, world.getTopLayerAt((int) x, (int) y))).size() > 0) {
                x = Math.round(entity.getX()) + (10 - random.nextInt(21));
                y = Math.round(entity.getY()) + 0.5 + (10 - random.nextInt(21));
            }
            setLocation(x, y, world.getTopLayerAt((int) x, (int) y));
            timer = 60 * random.nextInt(60 * 4);
        }
        newDes = !super.performTask(world, entity);
        timer--;
        return !newDes;
    }

    @Override
    public boolean isFinished(NpcBase entity) {
        return true;
    }
}
