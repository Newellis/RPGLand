package com.tynellis.Events;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Orginization.EntityQuadTree;
import com.tynellis.World.World;

public class EntityTickEvent extends TickEvent {
    private final World world;
    private Entity entity;

    public EntityTickEvent(Entity entity, World world) {
        this.world = world;
        this.entity = entity;
    }

    @Override
    public void run(EventHandler handler) {
            EntityQuadTree collisionTree = world.getCollisionTree();
            collisionTree.remove(entity);
            entity.tick(world, world.getEntitiesIntersecting(entity.getBounds()));
            collisionTree.insert(entity);
            super.run(handler);
    }
}