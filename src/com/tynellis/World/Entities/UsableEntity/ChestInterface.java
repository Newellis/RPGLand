package com.tynellis.World.Entities.UsableEntity;

import com.tynellis.World.Items.ItemPile;

public class ChestInterface implements UsingInterface {
    private Chest chest;

    public ChestInterface(Chest chest) {
        this.chest = chest;
    }

    public void addItem(ItemPile pile) {
        chest.addItem(pile);
    }

    public boolean isValid() {
        return chest != null;
    }

    public void stopUsing() {
        chest.removeInterface(this);
        chest = null;
    }

    public String toString() {
        return chest.toString();
    }

    public boolean canAddItem(ItemPile pile) {
        return chest.canAddItem(pile);
    }
}
