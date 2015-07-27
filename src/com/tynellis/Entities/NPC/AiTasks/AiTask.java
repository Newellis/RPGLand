package com.tynellis.Entities.NPC.AiTasks;

import com.tynellis.Entities.Entity;
import com.tynellis.World.World;

public interface AiTask{
    boolean performTask(World world, Entity e);
}
