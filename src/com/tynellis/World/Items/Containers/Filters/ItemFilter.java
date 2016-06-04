package com.tynellis.World.Items.Containers.Filters;

import com.tynellis.World.Items.ItemPile;

import java.io.Serializable;

public abstract class ItemFilter implements Serializable {
    public enum Type {
        WhiteList,
        BlackList,
    }

    protected Type type = Type.WhiteList;

    public ItemFilter(Type type) {
        this.type = type;
    }

    public abstract boolean followsFilter(ItemPile pile);

    public void filterType(Type type) {
        this.type = type;
    }
}
