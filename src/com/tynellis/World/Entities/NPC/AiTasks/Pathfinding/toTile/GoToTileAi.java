package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toTile;

import com.tynellis.World.Entities.NPC.AiTasks.AiTask;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.Core.PathfinderAi;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.util.Random;

public class GoToTileAi extends AiTask {
    Class tileType;
    Tile closest;
    int x, y, z;
    protected int range;


    public GoToTileAi(Class type, int range) {
        tileType = type;
        this.range = range;
    }

    @Override
    public boolean performTask(Region region, Random random, NpcBase entity) {
        PathfinderAi pathfinder = entity.getPathfinder();
        if (findTarget(region, entity, random)) {
            if (pathfinder.getCurrentActivity() != this) {
                pathfinder.setCurrentActivity(this);
                pathfinder.setRanges(range, 0);
            }
            if (pathfinder.heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(x, y, z)) > 0) {
                pathfinder.setLocation(x, y, z);
                boolean pathfind = pathfinder.performTask(region, random, entity);
                if (!pathfind) {
                    entity.setMoving(false);
                    closest = null;
                }
                return pathfind;
            } else {
                entity.setMoving(false);
                return false;
            }
        }
        entity.setMoving(false);
        return false;
    }

    boolean findTarget(Region region, NpcBase entity, Random rand) {
        if (tileType.isInstance(closest)) {
            return true;
        }
        Rectangle center = entity.getBounds();
        int x = (int) (center.x + (center.width / 2.0)) / Tile.WIDTH;
        int y = (int) (center.y + (center.height / 2.0)) / Tile.HEIGHT;
        int[] point = region.getLocationOfNearestTile(tileType, x, y, (int) entity.getZ(), range);

        if (point != null) {
            this.x = point[0];
            this.y = point[1];
            this.z = point[2];
            closest = region.getTile(x, y, z);
            if (tileType.isInstance(closest)) {
                return true;
            }
        }
        closest = null;
        return false;
    }

    @Override
    public boolean isFinished(NpcBase entity) {
        return entity.getPathfinder().heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(x, y, z)) < 0.5;
    }
}
