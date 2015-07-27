package com.tynellis.Entities;

import com.sun.tools.javac.code.Attribute;
import com.tynellis.World.World;

import java.awt.Graphics;

public abstract class Mob extends Entity {

    private int health = 20;
    public Mob(int x, int y, int z, int width, int height) {
        super(x, y, z, width, height);
    }

    public void tick(World world) {
        super.tick(world);
    }
}
