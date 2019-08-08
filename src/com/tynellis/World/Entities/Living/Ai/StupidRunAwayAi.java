package com.tynellis.World.Entities.Living.Ai;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.ArrayList;
import java.util.Random;

public class StupidRunAwayAi extends AiTask {
    private Class avoidType;
    private double distance;

    public StupidRunAwayAi(Class avoid, double distance) {
        avoidType = avoid;
        this.distance = distance;
    }

    @Override
    public boolean performTask(Region region, Random random, LivingEntity entity) {
        ArrayList<Entity> near = region.getEntitiesNearEntity(entity, distance);
        ArrayList<Entity> avoid = new ArrayList<Entity>();
        for (Entity e : near) {
            if (avoidType.isInstance(e) && e != entity) {
                avoid.add(e);
            }
        }
        if (avoid.size() <= 0) {
            return false;
        }
        double x, y;
        x = y = 0;
        for (int i = 0; i < avoid.size(); i++) {
            Entity e = avoid.get(i);
            x += e.getX();
            y += e.getY();
        }
        x = x / avoid.size();
        y = y / avoid.size();
        entity.setFacing(faceAwayFrom(entity, x, y));
        entity.setMoving(true);
        return true;
    }

    private double faceAwayFrom(Entity e, double x, double y) {
        double faceX, faceY;
        faceX = e.getX() + (e.getX() - x);
        faceY = e.getY() + (e.getY() - y);
        return FaceClosestAi.facingPoint(e, faceX, faceY);
    }

    @Override
    public boolean isFinished(LivingEntity entity) {
        return true;
    }
}
