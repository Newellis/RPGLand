package com.tynellis.World.Items;

import com.tynellis.World.Entities.Tree;

public class TreeSeeds extends Item {
    private Tree.Type seedType;

    public TreeSeeds(Tree.Type type) {
        super("TreeSeeds", 3, 0);
        seedType = type;
        if (type == Tree.Type.Oak) {
            setName("Acorn");
        } else if (type == Tree.Type.Pine) {
            setName("Pine Cone");
            setArt(3, 1);
        }
    }
}
