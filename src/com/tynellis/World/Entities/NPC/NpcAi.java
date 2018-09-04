package com.tynellis.World.Entities.NPC;

import com.tynellis.World.Entities.NPC.AiTasks.AiTask;
import com.tynellis.World.world_parts.Regions.Region;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public void tick(Region region, Random random, NpcBase npc) {
        for (List<AiTask> tasks : aiList) {
            boolean success = true;
            for (AiTask task : tasks) {
                success &= task.tryTask(region, random, npc);
            }
            if (success) {
                return;
            }
        }
    }
}
