package com.tynellis.World.Items;

import java.io.Serializable;

public class ItemPile implements Serializable {
    private Item item;
    private int size;

    public ItemPile(Item item) {
        this(item, 1);
    }

    public ItemPile(Item item, int quantity) {
        this.item = item;
        size = quantity;
    }

    public boolean canAddPile(ItemPile pile) {
        if (pile.item.getClass() == item.getClass() && item.getName().equals(pile.item.getName())) {
            if (size + pile.size <= item.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public boolean canAddPartOfPile(ItemPile pile) {
        if (pile.item.getClass() == item.getClass() && item.getName().equals(pile.item.getName())) {
            if (size < item.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public void addPile(ItemPile pile) {
        if (canAddPartOfPile(pile)) {
            if (canAddPile(pile)) {
                size += pile.size;
                pile.size = 0;
            } else {
                pile.size -= item.getMaxStackSize() - size;
                size = item.getMaxStackSize();
            }
        }
    }

    public int removeFromPile(int num) {
        if (size <= num) {
            size = 0;
            return size;
        }
        size -= num;
        return num;
    }

    public String toString() {
        return size + " " + item.getName();
    }

    public Item getItem() {
        return item;
    }

    public int getSize() {
        return size;
    }

    public boolean isSame(ItemPile pile) {
        return item.getName().equals(pile.getItem().getName()) && size == pile.getSize();
    }

    public void replaceStack(ItemPile pile) {
        item = pile.item;
        size = pile.size;
    }
}
