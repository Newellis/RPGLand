package com.tynellis.Entities;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Tree extends Entity {
    private static final SpriteSheet trunk = new SpriteSheet("tempArt/lpc/core/tiles/trunk.png", 96, 96, 1);
    private static final SpriteSheet top = new SpriteSheet("tempArt/lpc/core/tiles/treetop.png", 96, 96, 1);
    private int treeType;
    private int treeHeight;

    public Tree(boolean oak, int x, int y, int z, Random rand) {
        super(x, y, z, 20,20);
        if (oak){
            treeType = 0;
        } else {
            treeType = 1;
        }
        treeHeight = 20 + rand.nextInt(20);
        speed = 0.0;
        canBeMoved = false;
    }

    public void tick(World world) {
        super.tick(world);
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        BufferedImage trunkFrame = trunk.getSprite(0).getStill(treeType);
        BufferedImage topFrame = top.getSprite(treeType).getStill(0);
        g.drawImage(trunkFrame, (int)((posX + 0.5) * Tile.WIDTH) + xOffset - (trunkFrame.getWidth() / 2), (int)(((posY + 0.5) * Tile.HEIGHT) + yOffset - trunkFrame.getHeight() + (1.5 * height)), null);
        g.drawImage(topFrame, (int)((posX + 0.5) * Tile.WIDTH) + xOffset - (topFrame.getWidth() / 2), (int)(((posY + 0.5) * Tile.HEIGHT) + yOffset - topFrame.getHeight()) - treeHeight, null);
        super.render(g,xOffset,yOffset);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return false;
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return false;
    }
}
