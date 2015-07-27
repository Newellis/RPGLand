package com.tynellis.Entities.NPC;

import com.tynellis.Entities.Entity;
import com.tynellis.Entities.NPC.AiTasks.AiTask;
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

    public void tick(World world, Entity e) {
        for (List<AiTask> tasks : aiList) {
            boolean success = true;
            for (AiTask task : tasks) {
                success &= task.performTask(world, e);
            }
            if (success) {
                return;
            }
        }
    }
}
