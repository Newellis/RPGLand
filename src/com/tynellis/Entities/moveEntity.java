package com.tynellis.Entities;

public class moveEntity {
    private final double lastY;
    private final Entity entity;

    public moveEntity(Entity e, double lastY) {
        this.lastY = lastY;
        entity = e;
    }

    public double getLastY() {
        return lastY;
    }

    public Entity getEntity() {
        return entity;
    }
}
