package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.toTile;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class ChangeTileAi extends GoToTileAi {
    private Tile newTile;

    public ChangeTileAi(Class type, Tile newTile, int range) {
        super(type, range);
        this.newTile = newTile;
    }

    public boolean performTask(Region region, Random random, NpcBase entity) {
        boolean task = super.performTask(region, random, entity);
        if (closest != null) {
            if (shouldChange(region, entity)) {
                return changeTile(region, random, entity);
            }
        }
        if (!task && closest != null) {
            return true;
        }
        return task;
    }

    protected boolean changeTile(Region region, Random random, NpcBase entity) {
        region.setTile(newTile.newTile(random, closest.getHeightInWorld()), x, y, z);
        region.updateTileArtAround(x, y);
        closest = null;
        return true;
    }

    protected boolean shouldChange(Region region, NpcBase entity) {
        boolean atClosest = (int) entity.getX() == x && (int) entity.getY() == y && (int) entity.getZ() == z && tileType.isInstance(closest);
        double distance = entity.getPathfinder().heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(x, y, z));
        boolean closeToClosest = distance <= 1;
        return atClosest || closeToClosest;
    }
}
