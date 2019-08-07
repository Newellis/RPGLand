package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.Core;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.util.Random;

public class PathfindInAreaAi extends PathfinderAi {

    private Rectangle area;

    public PathfindInAreaAi(Rectangle area) {
        super();
        this.area = area;
    }

    public boolean performTask(Region region, Random random, NpcBase entity) {
        if (area.contains(destX, destY)) {
            return super.performTask(region, random, entity);
        }
        return false;
    }
}