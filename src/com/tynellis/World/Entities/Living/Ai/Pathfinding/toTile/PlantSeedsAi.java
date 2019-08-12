package com.tynellis.World.Entities.Living.Ai.Pathfinding.toTile;

import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Seed;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.LandTiles.Natural.Dirt;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlantSeedsAi extends GoToTileAi {
    private Rectangle farm;
    private ArrayList<Class> seedTypes;
    private boolean outOfFarm = true;

    public PlantSeedsAi(Rectangle farm, ArrayList<Class> seeds) {
        super(Dirt.class, farm.width + farm.height);
        this.farm = farm;
        seedTypes = seeds;
    }

    public boolean performTask(Region region, Random random, LivingEntity entity) {
        boolean task = super.performTask(region, random, entity);
        if (closest != null) {
            if (shouldPlant(region, entity)) {
                return Plant(region, random, entity);
            }
        }
        if (!task && closest != null) {
            return true;
        }
        return task;
    }

    private boolean Plant(Region region, Random random, LivingEntity entity) {
        System.out.println(entity.getName() + " wants to plant a " + getSeed(entity).getItem().getName() + " at " + x + ", " + y + ", " + z);
        return false;
    }

    private ItemPile getSeed(LivingEntity entity) {
        for (ItemPile pile : entity.getInventory().getContents()) {
            for (Class<Seed> type : seedTypes) {
                if (type.isInstance(pile.getItem()) && pile.getSize() > 0) {
                    return pile;
                }
            }
        }
        return null;
    }

    private boolean shouldPlant(Region region, LivingEntity entity) {
        boolean atClosest = (int) entity.getX() == x && (int) entity.getY() == y && (int) entity.getZ() == z && tileType.isInstance(closest);
        double distance = entity.getPathfinder().heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(x, y, z));
        boolean closeToClosest = distance <= 1;
        return (atClosest || closeToClosest) && hasSeeds(entity);
    }

    boolean findTarget(Region region, LivingEntity entity, Random rand) {
        if (outOfFarm || tileType.isInstance(closest)) {
            return true;
        }
        if (hasSeeds(entity)) {
            Rectangle center = entity.getBounds();
            int[] point;
            if (farm.contains(center)) {
                if (outOfFarm) {
                    outOfFarm = false;
                    closest = null;
                }
                int x = ((int) (center.x + (center.width / 2.0)) / Tile.WIDTH);
                int y = ((int) (center.y + (center.height / 2.0)) / Tile.HEIGHT);
                point = region.getLocationOfNearestTile(tileType, x, y, (int) entity.getZ(), range);
            } else {
                point = new int[]{farm.x + (farm.width / 2), farm.y + (farm.height / 2), region.getTopLayerAt(farm.x + (farm.width / 2), farm.y + (farm.height / 2))};
                outOfFarm = true;
                System.out.println("walking to farm");
            }
            if (point != null) {
                this.x = point[0];
                this.y = point[1];
                this.z = point[2];
                closest = region.getTile(x, y, z);
                if (outOfFarm || tileType.isInstance(closest)) {
                    return true;
                }
            }
        }
        closest = null;
        return false;
    }

    private boolean hasSeeds(LivingEntity entity) {
        for (ItemPile pile : entity.getInventory().getContents()) {
            for (Class<Seed> type : seedTypes) {
                if (pile != null && type.isInstance(pile.getItem()) && pile.getSize() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
