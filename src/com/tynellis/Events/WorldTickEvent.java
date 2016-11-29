package com.tynellis.Events;

import com.tynellis.World.World;

public class WorldTickEvent extends TickEvent {
    private World world;

    public WorldTickEvent(World world) {
        this.world = world;
    }

    @Override
    public void run(EventHandler handler) {
        world.tick();
        super.run(handler);
    }
}
