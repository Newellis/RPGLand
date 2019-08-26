package com.tynellis.World.Tiles.LandTiles;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.Tile;

import java.awt.*;
import java.util.Random;

public abstract class LayeredTile extends LandTile {
    private Tile base;
    private int artRow = 3, artCol = 1;
    private boolean fillsAir = false;
    private boolean blocksAir = false;

    public LayeredTile(String name, SpriteSheet sheet, Random rand, double altPercent, TileRank rank, int height, Tile base) {
        super(name, sheet, rand, altPercent, rank, height);
        this.base = base;
    }

    @Override
    public void render(Graphics g, int x, int y) {
        base.render(g, x, y);
        g.drawImage(getSheet(this).getSprite(artRow).getStill(artCol), x, y, null);
    }

    public void changeArtPos(int artRow, int artCol) {
        this.artRow = artRow;
        this.artCol = artCol;
    }

    public void updateArt(Tile[][] adjacent) {
        super.updateArt(adjacent);
        base.updateArt(adjacent);
    }

    public SpriteSheet getSheet(Tile tile) {
        return base.getSheet(tile);
    }

    public Tile getBase() {
        return base;
    }

    public void debugRender(Color color) {
        super.debugRender(color);
        base.debugRender(color);
    }

    public void setFull(boolean full, boolean blocks) {
        fillsAir = full;
        blocksAir = blocks;
    }

    public boolean isFull() {
        return fillsAir;
    }

    public boolean isBlocked() {
        return blocksAir;
    }
}
