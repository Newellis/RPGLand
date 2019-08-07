package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.Core.PathfinderAi;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class StayNearPoint extends PathfinderAi {
    private int range;
    private boolean returning = false;

    public StayNearPoint(int x, int y, int z, int range) {
        super(x, y, z, Integer.MAX_VALUE);
        this.range = range;
    }

    @Override
    public boolean performTask(Region region, Random random, NpcBase entity) {
        super.performTask(region, random, entity);
        double pathLength = getPathLength();
        if (pathLength < range / 2) {
            returning = false;
            entity.setMoving(false);
        } else if (pathLength > range && doesntInterrupt(entity)) {
            returning = true;
        }
        return returning;//(path.size() == 0 || !pathIsValid(world, entity));
    }

    @Override
    protected boolean moveAlongPath(Entity e) {
        if (returning) {
            boolean move = super.moveAlongPath(e);
            return move;
        } else {
            e.setMoving(false);
            return true;
        }
    }

    @Override
    public boolean isFinished(NpcBase entity) {
        return !returning;
    }
}
