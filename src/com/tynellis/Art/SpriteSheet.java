package com.tynellis.Art;

import java.awt.image.BufferedImage;

public class SpriteSheet extends SpriteImage {
    private Sprite[] sheet;

    public SpriteSheet(String path, int width, int height, int scale) {
        this(path, width, height, 0, 0, scale);
    }

    public SpriteSheet(String path, int width, int height, int xOffset, int yOffset, int scale) {
        BufferedImage[][] sheet = slice(path, width, height, xOffset, yOffset, scale);
        this.sheet = new Sprite[sheet.length];
        for (int i = 0; i < sheet.length; i++) {
            this.sheet[i] = new Sprite(sheet[i]);
        }
    }

    public Sprite getSprite(int row) {
        return sheet[row];
    }

    @Override
    protected void flipHoriz(boolean hFlipped) {
    }

    @Override
    protected void flipVert(boolean vFlipped) {
    }
}
