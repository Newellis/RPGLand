package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.World;

public class StayNearPoint extends PathfinderAi {
    private int range;
    private boolean returning = false;

    public StayNearPoint(int x, int y, int z, int range) {
        super(x, y, z, Integer.MAX_VALUE);
        this.range = range;
    }

    @Override
    public boolean performTask(World world, KillableEntity entity) {
        super.performTask(world, entity);
        double pathLength = getPathLength();
        if (pathLength < range / 2) {
            returning = false;
            entity.setMoving(false);
        } else if (pathLength > range && doesntInterrupt()) {
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
    public boolean isFinished() {
        return !returning;
    }
}
