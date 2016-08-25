package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.World.Entities.KillableEntity;

/**
 * Created by tyler on 6/2/16.
 */
public abstract class UsableEntity extends KillableEntity {

    public UsableEntity(double x, double y, double z, int width, int height) {
        super(x, y, z, width, height);
    }

    public abstract Object use(KillableEntity entity);

    public boolean canUse(KillableEntity entity) {
        double x, y, z, scoreLeft = 0;
        x = Math.abs(entity.getX() - getX());
        y = Math.abs(entity.getY() - getY());
        z = Math.abs(entity.getZ() - getZ());
        if (x < y) {
            scoreLeft += y - x;
            scoreLeft += Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
            scoreLeft += z;
        } else if (y < x) {
            scoreLeft += x - y;
            scoreLeft += Math.sqrt(Math.pow(y, 2) + Math.pow(y, 2));
            scoreLeft += z;
        } else {
            scoreLeft += Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            scoreLeft += z;
        }
        return scoreLeft < 2;
    }
}
