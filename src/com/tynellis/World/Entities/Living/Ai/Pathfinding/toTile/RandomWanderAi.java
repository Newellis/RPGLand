package com.tynellis.World.Entities.Living.Ai.Pathfinding.toTile;

import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.util.Random;

public class RandomWanderAi extends GoToTileAi {
    private boolean wandering = true;
    private int resting;
    private int maxRest;

    public RandomWanderAi(int range, int maxCoolDown) {
        super(null, range);
        maxRest = maxCoolDown;
    }

    public boolean performTask(Region region, Random random, LivingEntity entity) {
        if (wandering || resting <= 0) {
            boolean task = super.performTask(region, random, entity);
            wandering = task;
            if (!wandering) {
                resting = random.nextInt(maxRest);
            }
            return task;
        } else {
            resting--;
            return false;
        }
    }

    boolean findTarget(Region region, LivingEntity entity, Random rand) {
        if (wandering) {
            return true;
        }
        Rectangle center = entity.getBounds();
        int x = ((int) (center.x + (center.width / 2.0)) / Tile.WIDTH) + (rand.nextInt(range) - (range / 2));
        int y = ((int) (center.y + (center.height / 2.0)) / Tile.HEIGHT) + (rand.nextInt(range) - (range / 2));


        if (!region.isTileObstructed(x, y, region.getTopLayerAt(x, y))) {
            Tile tile = region.getTile(x, y, region.getTopLayerAt(x, y));
            this.x = x;
            this.y = y;
            this.z = region.getTopLayerAt(x, y);
            return true;
        }

        closest = null;
        return false;
    }
}
