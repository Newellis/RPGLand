package com.tynellis.World.Entities.Living.Ai.Pathfinding.toTile;

import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Items.Containers.Filters.ItemFilter;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Seed;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.LandTiles.ManMade.SeedHill;
import com.tynellis.World.Tiles.LandTiles.ManMade.TilledSoil;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.util.Random;

public class PlantSeedsAi extends GoToTileAi {
    private Rectangle farm;
    private ItemFilter seedTypes;
    private int spacing;
    private int performing = 0, maxPerforming = 30;


    public PlantSeedsAi(Rectangle farm, ItemFilter SeedsFilter) {
        this(farm, SeedsFilter, 1);
    }

    public PlantSeedsAi(Rectangle farm, ItemFilter SeedsFilter, int spacing) {
        this(farm, SeedsFilter, TilledSoil.class, spacing);
    }

    public PlantSeedsAi(Rectangle farm, ItemFilter SeedsFilter, Class farmland, int spacing) {
        super(farmland, farm.width + farm.height);
        this.farm = farm;
        seedTypes = SeedsFilter;
        this.spacing = spacing;
    }

    public PlantSeedsAi(ItemFilter SeedsFilter, Class farmland, int range) {
        super(farmland, range);
        this.farm = null;
        seedTypes = SeedsFilter;
        this.spacing = 1;
    }



    public boolean performTask(Region region, Random random, LivingEntity entity) {
        boolean task = super.performTask(region, random, entity);
        if (closest != null) {
            if (shouldPlant(region, entity)) {
                if (performing != 0) {
                    performing--;
                    return true;
                }
                boolean plant = Plant(region, random, entity);
                if (plant) {
                    performing = maxPerforming;
                }
                return plant;
            }
        }
        if (!task && closest != null) {
            return true;
        }
        return task;
    }

    private boolean Plant(Region region, Random random, LivingEntity entity) {
        ItemPile seed = getSeed(entity);
        if (seed != null) {
            region.setTile(new SeedHill((Seed) seed.getItem(), random, closest), x, y, z);
            seed.removeFromPile(1);
            closest = null;
            return true;
        }
        return false;
    }

    private ItemPile getSeed(LivingEntity entity) {
        for (ItemPile pile : entity.getInventory().getContents()) {
            if (pile != null) {
                if (seedTypes.followsFilter(pile) && pile.getSize() > 0) {
                    return pile;
                }
            }
        }
        return null;
    }

    private boolean shouldPlant(Region region, LivingEntity entity) {
        boolean desIsClosest = closest == region.getTile(x, y, z);
        boolean atClosest = (int) entity.getX() == x && (int) entity.getY() == y && (int) entity.getZ() == z && tileType.isInstance(closest);
        double distance = entity.getPathfinder().heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(x, y, z));
        boolean closeToClosest = distance <= 1;
        return desIsClosest && (atClosest || closeToClosest) && hasSeeds(entity);
    }

    boolean findTarget(Region region, LivingEntity entity, Random rand) {
        if (tileType.isInstance(closest)) {
            return true;
        }

        if (hasSeeds(entity)) {
            if (farm != null) {
                Tile found = null;
                int[] point = null;
                double distance = Double.MAX_VALUE;
                for (int X = farm.x; X < farm.x + farm.width; X += spacing) {
                    for (int Y = farm.y; Y < farm.y + farm.height; Y += spacing) {
                        int Z = region.getTopLayerAt(X, Y);
                        Tile tile = region.getTile(X, Y, Z);
                        if (tileType.isInstance(tile)) {
                            double testDist = entity.getPathfinder().heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(X, Y, Z));
                            if (testDist < distance) {
                                found = tile;
                                distance = testDist;
                                point = new int[]{X, Y, Z};
                            }
                        }
                    }
                }

                if (found != null && distance <= range) {
                    closest = found;
                    x = point[0];
                    y = point[1];
                    z = point[2];
                    return true;
                }
            } else {
                return super.findTarget(region, entity, rand);
            }
        }
        closest = null;
        return false;
    }

    private boolean hasSeeds(LivingEntity entity) {
        return getSeed(entity) != null;
    }
}
