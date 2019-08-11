package com.tynellis.World.Entities.Plants.Crops.Seeds;

import com.tynellis.World.Entities.Plants.Tree;

import java.util.Random;

public class TreeSeed extends Seedling {
    private Tree.Type type;

    public TreeSeed(Tree.Type type, double x, double y, double z, Random random) {
        super(x, y, z, random, 2);
        this.type = type;
    }


    @Override
    protected void Grow(Random random) {
        increaseGrowthStage();
    }
}
