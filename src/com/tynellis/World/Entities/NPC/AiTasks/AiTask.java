package com.tynellis.World.Entities.NPC.AiTasks;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.world_parts.Regions.Region;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AiTask implements Serializable {
    private List<AiTask> noInterrupt = new ArrayList<AiTask>();

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    public abstract boolean performTask(Region region, Random random, NpcBase entity);

    public abstract boolean isFinished(NpcBase entity);

    public boolean tryTask(Region region, Random random, NpcBase entity) {
        if (doesntInterrupt(entity)) {
            return performTask(region, random, entity);
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
