package com.tynellis.World.Items.Containers;

import com.tynellis.World.Items.Containers.Filters.ItemFilter;
import com.tynellis.World.Items.ItemPile;

import java.io.Serializable;
import java.util.Arrays;

public class Container implements Serializable {
    private ContainerSlot[] inventory;
    private ItemFilter filter = null;

    public Container(int size) {
        inventory = new ContainerSlot[size];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = new ContainerSlot();
        }
    }

    public Container(ItemFilter[] filters) {
        inventory = new ContainerSlot[filters.length];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = new ContainerSlot();
            inventory[i].setFilter(filters[i]);
        }
    }

    public void tick() {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                inventory[i] = new ContainerSlot();
            }
            if (inventory[i].getPile() != null && inventory[i].getPile().getSize() <= 0) {
                inventory[i].setPile(null);
            }
        }
    }

    public void addItemPile(ItemPile pile) {
        if (pile.getSize() <= 0) {
            return;
        }
        int firstNullAt = -1;
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i].getPile() == null && firstNullAt == -1 && inventory[i].canHoldItem(pile)) {
                firstNullAt = i;
            }
            if (inventory[i].getPile() != null && inventory[i].getPile().canAddPartOfPile(pile)) {
                inventory[i].getPile().addPile(pile);
                if (pile.getSize() <= 0) {
                    return;
                }
            }
        }
        if (firstNullAt > -1) {
            if (pile.getSize() <= pile.getItem().getMaxStackSize()) {
                inventory[firstNullAt].setPile(new ItemPile(pile.getItem(), pile.getSize()));
                pile.removeFromPile(pile.getSize());
            } else {
                inventory[firstNullAt].setPile(new ItemPile(pile.getItem(), pile.getItem().getMaxStackSize()));
                pile.removeFromPile(pile.getItem().getMaxStackSize());
            }
        }
    }

    public ItemPile[] getContents() {
        ItemPile[] contents = new ItemPile[inventory.length];
        for (int i = 0; i < inventory.length; i++) {
            contents[i] = inventory[i].getPile();
        }
        return contents;
    }

    public String toString() {
        return "Inventory " +
                "Size: " + inventory.length +
                " Contents: " + Arrays.toString(inventory);
    }

    public boolean canAddItem(ItemPile pile) {
        if (pile == null || filter != null && !filter.followsFilter(pile)) {
            return false;
        }
        for (ContainerSlot slot : inventory) {
            if (slot.canHoldItem(pile)) {
                return true;
            }
        }
        return false;
    }

    public void setFilter(ItemFilter filter) {
        this.filter = filter;
    }

    public ContainerSlot getSlot(int i) {
        return inventory[i];
    }

    public void setSlot(int i, ItemPile pile) {
        inventory[i].setPile(pile);
    }

    public void setAllSlotFilters(ItemFilter filter) {
        for (ContainerSlot slot : inventory) {
            slot.setFilter(filter);
        }
    }

    public ContainerSlot[] getInventory() {
        return inventory;
    }

    public boolean isFull() {
        for (ContainerSlot slot : inventory) {
            if (slot.getPile() == null || slot.getPile().getSize() < slot.getPile().getItem().getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public void consumeItem(ItemPile item) {
        if (canConsumeItem(item)) {
            int removed = 0;
            for (ItemPile consume : getContents()) {
                if (consume != null && item.getItem().getName().equals(consume.getItem().getName())) {
                    if (consume.getSize() <= item.getSize() - removed) {
                        removed += consume.getSize();
                        consume.removeFromPile(consume.getSize());
                    } else {
                        consume.removeFromPile(item.getSize() - removed);
                        return;
                    }
                    if (removed >= item.getSize()) {
                        return;
                    }
                }
            }
        }
    }

    private boolean canConsumeItem(ItemPile item) {
        if (item == null) return false;
        int holding = 0;
        for (ItemPile consume : getContents()) {
            if (consume != null && item.getItem().getName().equals(consume.getItem().getName())) {
                holding += item.getSize();
            }
        }
        return holding >= item.getSize();
    }
}
