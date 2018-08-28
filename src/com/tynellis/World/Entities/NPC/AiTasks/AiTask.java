package com.tynellis.World.Entities.NPC.AiTasks;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.World;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AiTask implements Serializable {
    private List<AiTask> noInterrupt = new ArrayList<AiTask>();

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public abstract boolean performTask(World world, NpcBase entity);

    public abstract boolean isFinished(NpcBase entity);

    public boolean tryTask(World world, NpcBase entity) {
        if (doesntInterrupt(entity)) {
            return performTask(world, entity);
        }
        return false;
    }

    protected boolean doesntInterrupt(NpcBase entity) {
        boolean goodToGo = true;
        for (AiTask task : noInterrupt) {
            goodToGo &= task.isFinished(entity);
        }
        return goodToGo;
    }

    public void dontInterrupt(AiTask task) {
        noInterrupt.add(task);
    }
}
