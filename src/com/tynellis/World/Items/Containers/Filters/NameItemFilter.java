package com.tynellis.World.Items.Containers.Filters;

import com.tynellis.World.Items.ItemPile;

public class NameItemFilter extends ItemFilter {

    private String[] filter;

    public NameItemFilter(String[] filter, Type type) {
        super(type);
        this.filter = filter;
    }

    public boolean followsFilter(ItemPile pile) {
        for (String aFilter : filter) {
            if (pile.getItem().getName().equals(aFilter)) {
                return type == Type.WhiteList;
            }
        }
        return type == Type.BlackList;
    }
}
