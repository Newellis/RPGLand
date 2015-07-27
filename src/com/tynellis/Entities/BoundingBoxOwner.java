package com.tynellis.Entities;

import java.awt.Rectangle;

public interface BoundingBoxOwner {
    Rectangle getBounds();
    void handleCollision(BoundingBoxOwner bb, double xMove, double yMove);
    boolean isPassableBy(Entity e);
    boolean isPassableBy(Entity.movementTypes movementType);
}
