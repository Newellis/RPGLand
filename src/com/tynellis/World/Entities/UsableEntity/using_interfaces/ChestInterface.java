package com.tynellis.World.Entities.UsableEntity.using_interfaces;

import com.tynellis.Menus.InGameMenus.ChestInventory;
import com.tynellis.Menus.InGameMenus.Inventory;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Entities.UsableEntity.Chest;
import com.tynellis.World.Items.Containers.ContainerSlot;
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

    public int getSize() {
        return chest.getInventory().getContents().length;
    }

    public ContainerSlot getItem(int i) {
        return chest.getInventory().getSlot(i);
    }

    public void setItem(int i, ItemPile pile) {
        chest.getInventory().setSlot(i, pile);
    }

    public void stopUsing() {
        if (isValid()) {
            chest.removeInterface(this);
            chest = null;
        }
    }

    @Override
    public Inventory getMenu(Player user) {
        return new ChestInventory(this, user);
    }

    public String toString() {
        return chest.toString();
    }

    public boolean canAddItem(ItemPile pile) {
        return chest.canAddItem(pile);
    }
}
