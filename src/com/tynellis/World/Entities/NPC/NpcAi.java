package com.tynellis.World.Entities.NPC;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.NPC.AiTasks.AiTask;
import com.tynellis.World.World;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NpcAi implements Serializable {
    List<List<AiTask>> aiList = new ArrayList<List<AiTask>>();

    public NpcAi() {

    }

    public void addTask(int precedence, AiTask task) {
        if (precedence >= aiList.size()) {
            precedence = aiList.size();
            aiList.add(new ArrayList<AiTask>());
        }
        aiList.get(precedence).add(task);
    }

    public void tick(World world, KillableEntity entity) {
        for (List<AiTask> tasks : aiList) {
            boolean success = true;
            for (AiTask task : tasks) {
                success &= task.tryTask(world, entity);
            }
            if (success) {
                return;
            }
        }
    }
}
