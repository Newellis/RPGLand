package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.world_parts.Region;

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
    public boolean performTask(Region region, Random random, NpcBase entity) {
        if (newDes || timer <= 0) {
            path.clear();
            double x = Math.round(entity.getX()) + (10 - this.random.nextInt(21)), y = Math.round(entity.getY()) + 0.5 + (10 - this.random.nextInt(21));
            while (region.getTile((int) x, (int) y, region.getTopLayerAt((int) x, (int) y)) == null || region.getEntitiesIntersecting(region.getTileBounds((int) x, (int) y, region.getTopLayerAt((int) x, (int) y))).size() > 0) {
                x = Math.round(entity.getX()) + (10 - this.random.nextInt(21));
                y = Math.round(entity.getY()) + 0.5 + (10 - this.random.nextInt(21));
            }
            setLocation(x, y, region.getTopLayerAt((int) x, (int) y));
            timer = 60 * this.random.nextInt(60 * 4);
        }
        newDes = !super.performTask(region, random, entity);
        timer--;
        return !newDes;
    }

    @Override
    public boolean isFinished(NpcBase entity) {
        return true;
    }
}
