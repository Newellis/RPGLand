package com.tynellis.World.Entities.Living.Ai.Pathfinding.Core;

import com.tynellis.World.Entities.Living.LivingEntity;

import java.awt.*;

public class PathfindInAreaAi extends PathfinderAi {

    private Rectangle area;

    public PathfindInAreaAi(Rectangle area) {
        super();
        this.area = area;
        range = area.width + area.height;
    }

    public boolean canGetTo(LivingEntity entity, double x, double y, double z) {
        return area.contains(x, y);
    }
}