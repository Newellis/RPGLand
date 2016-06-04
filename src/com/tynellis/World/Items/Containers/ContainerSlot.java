package com.tynellis.World.Items.Containers;


import com.tynellis.World.Items.Containers.Filters.ItemFilter;
import com.tynellis.World.Items.ItemPile;

import java.io.Serializable;

public class ContainerSlot implements Serializable {

    private ItemPile pile;
    private ItemFilter filter = null;

    public void setPile(ItemPile pile) {
        this.pile = pile;
    }

    public ItemPile getPile() {
        return pile;
    }

    public boolean canHoldItem(ItemPile pile) {
        if (filter != null && !filter.followsFilter(pile)) {
            return false;
        }
        return isEmpty() || this.pile.canAddPartOfPile(pile);
    }

    public boolean isEmpty() {
        return pile == null;
    }

    public void setFilter(ItemFilter filter) {
        this.filter = filter;
    }

    public String toString() {
        if (pile != null) {
            return pile.toString();
        }
        return "null";
    }
}
