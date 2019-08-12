package com.tynellis.World.Entities.Living.Ai.Pathfinding.Core;

import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Nodes.Node;

public class PathfindInRangeAi extends PathfinderAi {

    private int radius;
    private int originX, originY, originZ;

    public PathfindInRangeAi(int radius, int x, int y, int z) {
        super();
        this.radius = radius;
        range = radius * 3;
        originX = x;
        originY = y;
        originZ = z;
    }

    public boolean canGetTo(LivingEntity entity, double x, double y, double z) {
        return heuristicCostEstimate(new Node(originX, originY, originZ), new Node(x, y, z)) < radius;
    }
}
