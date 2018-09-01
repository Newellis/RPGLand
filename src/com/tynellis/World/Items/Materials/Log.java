package com.tynellis.World.Items.Materials;

import com.tynellis.World.Entities.Plants.Tree;
import com.tynellis.World.Items.Item;

public class Log extends Item {
    public Log(Tree.Type type) {
        super("Log", 20, 0, 0);
        setName(type.name() + " Log");
        if (type == Tree.Type.Pine) {
            setArt(0, 0);
        }
    }
}