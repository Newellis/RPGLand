package com.tynellis.World.Items.Containers.Filters;

import com.tynellis.World.Items.ItemPile;

public class StackItemFilter extends ItemFilter {

    private ItemPile[] filter;

    public StackItemFilter(ItemPile[] filter, Type type) {
        super(type);
        this.filter = filter;
    }

    @Override
    public boolean followsFilter(ItemPile pile) {
        for (ItemPile aFilter : filter) {
            if (aFilter.isSame(pile)) {
                return type == Type.WhiteList;
            }
        }
        return type == Type.BlackList;
    }
}
