package com.tynellis.World.Items.Containers.Filters;

import com.tynellis.World.Items.ItemPile;

public class QuantityFilter extends ItemFilter {
    public QuantityFilter(Type type) {
        super(type);
    }

    @Override
    public boolean followsFilter(ItemPile pile) {
        return false;
    }
}
