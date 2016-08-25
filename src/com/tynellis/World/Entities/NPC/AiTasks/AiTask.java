package com.tynellis.World.Entities.NPC.AiTasks;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.World;

import java.util.ArrayList;
import java.util.List;

public abstract class AiTask {
    private List<AiTask> noInterrupt = new ArrayList<AiTask>();

    public abstract boolean performTask(World world, KillableEntity entity);

    public abstract boolean isFinished();

    public boolean tryTask(World world, KillableEntity entity) {
        if (doesntInterrupt()) {
            return performTask(world, entity);
        }
        return false;
    }

    protected boolean doesntInterrupt() {
        boolean goodToGo = true;
        for (AiTask task : noInterrupt) {
            goodToGo &= task.isFinished();
        }
        return goodToGo;
    }

    public void dontInterrupt(AiTask task) {
        noInterrupt.add(task);
    }
}
