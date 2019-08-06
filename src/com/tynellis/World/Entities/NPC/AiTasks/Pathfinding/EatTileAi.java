package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Entities.NPC.animals.Animal;
import com.tynellis.World.Items.Food.Food;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class EatTileAi extends ChangeTileAi {
    private boolean validFood = true;

    public EatTileAi(Class type, Tile newTile, int range) {
        super(type, newTile, range);
        if (!Food.class.isAssignableFrom(type)) {
            System.out.println("INVALID FOOD CLASS");
            validFood = false;
        }
    }

    public boolean performTask(Region region, Random random, NpcBase entity) {
        if (closest == null) {
            System.out.println("Hungry? for " + closest);
            if (entity instanceof Animal && ((Animal) entity).canEat()) {
                System.out.println("Does the " + entity.getName() + " want Food");
                if (closest == null && ((Animal) entity).wantsToEat(random)) {
                    System.out.println("Wants Food");
                } else {
                    System.out.println("No to Food");
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

    protected boolean shouldChange(Region region, NpcBase entity) {
        System.out.println("Eat? " + closest);
        if (((Animal) entity).canEat()) {
            return super.shouldChange(region, entity);
        } else return false;
    }

    protected boolean changeTile(Region region, Random random, NpcBase entity) {
        ((Animal) entity).eatFood((Food) closest);
        System.out.println("Eat!!!!!!!!!");
        return super.changeTile(region, random, entity);
    }
}
