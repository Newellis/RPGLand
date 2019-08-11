package com.tynellis.World.Items;

import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.Plants.Tree;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class TreeSeeds extends Seed {
    private Tree.Type seedType;

    public TreeSeeds(Tree.Type type, Random rand) {
        super(new Tree(type, rand), 3, 0);
        seedType = type;
        if (type == Tree.Type.Oak) {
            setName("Acorn");
        } else if (type == Tree.Type.Pine) {
            setName("Pine Cone");
            setArt(3, 1);
        }
    }

    @Override
    public boolean use(Region region, Random random, KillableEntity user) {
        return false;
    }

    @Override
    public boolean canUse(Region region, KillableEntity user) {
        return false;
    }
}
