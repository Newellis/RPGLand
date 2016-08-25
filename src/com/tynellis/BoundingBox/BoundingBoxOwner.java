package com.tynellis.BoundingBox;

import com.tynellis.World.Entities.Entity;

import java.awt.Rectangle;

public interface BoundingBoxOwner {
    Rectangle getBounds();

    void handleCollision(BoundingBoxOwner bb, double xMove, double yMove, boolean isOver);

    boolean isPassableBy(Entity e);

    boolean isPassableBy(Entity.movementTypes movementType);
}
