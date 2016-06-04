package com.tynellis.World.Items.Containers.Filters;

import com.tynellis.World.Items.ItemPile;

public class MixedItemFilter extends ItemFilter {
    private ItemFilter[] filters;

    public MixedItemFilter(ItemFilter[] filter, Type type) {
        super(type);
        filters = filter;
    }

    public boolean followsFilter(ItemPile pile) {
        for (ItemFilter aFilter : filters) {
            if (aFilter.followsFilter(pile)) {
                return type == Type.WhiteList;
            }
        }
        return type == Type.BlackList;
    }
}
