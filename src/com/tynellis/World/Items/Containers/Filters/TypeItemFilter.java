package com.tynellis.World.Items.Containers.Filters;

import com.tynellis.World.Items.ItemPile;

public class TypeItemFilter extends ItemFilter {

    private Class[] filter;

    public TypeItemFilter(Class[] filter, Type type) {
        super(type);
        this.filter = filter;
    }

    public boolean followsFilter(ItemPile pile) {
        for (Class aFilter : filter) {
            if (aFilter.isInstance(pile.getItem())) {
                return type == Type.WhiteList;
            }
        }
        return type == Type.BlackList;
    }
}
