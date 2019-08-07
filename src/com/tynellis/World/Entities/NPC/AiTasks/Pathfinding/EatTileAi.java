package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Entities.NPC.animals.Animal;
import com.tynellis.World.Items.Food.Food;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.util.Random;

public class EatTileAi extends ChangeTileAi {
    private boolean validFood = true;
    private boolean done;

    public EatTileAi(Class type, Tile newTile, int range) {
        super(type, newTile, range);
        if (!Food.class.isAssignableFrom(type)) {
            System.out.println("INVALID FOOD CLASS");
            validFood = false;
        }
    }

    public boolean performTask(Region region, Random random, NpcBase entity) {
        if (closest == null) {
            if (entity instanceof Animal && ((Animal) entity).canEat()) {
                if (closest == null && ((Animal) entity).wantsToEat(random)) {
                    done = false;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        boolean task = super.performTask(region, random, entity);
        if (!task) {
            closest = null;
        }
        return task;
    }

    boolean findTarget(Region region, NpcBase entity, Random rand) {
        if (tileType.isInstance(closest)) {
            return true;
        }
        Rectangle center = entity.getBounds();
        int x = ((int) (center.x + (center.width / 2.0)) / Tile.WIDTH) + (rand.nextInt(range / 2) - (range / 4));
        int y = ((int) (center.y + (center.height / 2.0)) / Tile.HEIGHT) + (rand.nextInt(range / 2) - (range / 4));
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

    protected boolean shouldChange(Region region, NpcBase entity) {
        if (((Animal) entity).canEat()) {
            return super.shouldChange(region, entity);
        } else return false;
    }

    protected boolean changeTile(Region region, Random random, NpcBase entity) {
        ((Animal) entity).eatFood((Food) closest);
        done = true;
        return super.changeTile(region, random, entity);
    }

    @Override
    public boolean isFinished(NpcBase entity) {
        return done;
    }
}
