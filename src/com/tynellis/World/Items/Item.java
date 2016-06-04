package com.tynellis.World.Items;

import com.tynellis.Art.SpriteSheet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class Item implements Serializable {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/items.png", 32, 32, 1);
    private transient BufferedImage image;
    private int artCol, artRow;
    private String name;
    private int maxStackSize;

    public Item(String name) {
        this(name, 100);
    }

    public Item(String name, int maxStackSize) {
        this.maxStackSize = maxStackSize;
        this.name = name;
    }

    public Item(String name, int artCol, int artRow) {
        this(name, 100, artCol, artRow);
    }

    public Item(String name, int maxStackSize, int artRow, int artCol) {
        this.maxStackSize = maxStackSize;
        this.name = name;
        this.artCol = artCol;
        this.artRow = artRow;
        image = SHEET.getSprite(artRow).getStill(artCol);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = SHEET.getSprite(artRow).getStill(artCol);
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setArt(int row, int col) {
        artRow = row;
        artCol = col;
        image = SHEET.getSprite(artRow).getStill(artCol);
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

}
