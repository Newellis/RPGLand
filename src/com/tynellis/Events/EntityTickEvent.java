package com.tynellis.Events;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.World;

public class EntityTickEvent extends TickEvent {
    private World world;
    private Entity entity;

    public EntityTickEvent(Entity entity, World world) {
        this.world = world;
        this.entity = entity;
    }

    @Override
    public void run(EventHandler handler) {
        entity.tick(world, world.getEntitiesIntersecting(entity.getBounds()));
        //if (!entity.isDead()) {
        super.run(handler);
        //}
    }
}
