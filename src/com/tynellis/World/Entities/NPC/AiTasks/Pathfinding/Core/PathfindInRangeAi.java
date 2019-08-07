package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.Core;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class PathfindInRangeAi extends PathfinderAi {

    private int range;
    private int originX, originY, originZ;

    public PathfindInRangeAi(int range, int x, int y, int z) {
        super();
        this.range = range;
        originX = x;
        originY = y;
        originZ = z;
    }

    public boolean performTask(Region region, Random random, NpcBase entity) {
        if (heuristicCostEstimate(new Node(originX, originY, originZ), new Node(destX, destY, destZ)) < range) {
            return super.performTask(region, random, entity);
        }
        return false;
    }
}
