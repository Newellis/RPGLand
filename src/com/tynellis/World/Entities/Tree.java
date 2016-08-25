package com.tynellis.World.Entities;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Items.TreeSeeds;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class Tree extends KillableEntity {
    private static final SpriteSheet trunk = new SpriteSheet("tempArt/lpc/core/tiles/trunk.png", 96, 96, 1);
    private static final SpriteSheet top = new SpriteSheet("tempArt/lpc/core/tiles/treetop.png", 96, 96, 1);
    private Type treeType;
    private int treeHeight;

    public enum Type {
        Oak,
        Pine,
    }

    public Tree(Type type, double x, double y, double z, Random rand) {
        super(x, y, z, 20, 20);
        treeType = type;
        treeHeight = 20 + rand.nextInt(20);
        speed = 0.0;
        canBeMoved = false;
    }

    @Override
    public ItemPile[] getItemsToDrop(Random rand) {
        ItemPile[] piles = new ItemPile[2];
        int amount = ((treeHeight) / (5 + rand.nextInt(6)));
        piles[0] = new ItemPile(new Log(treeType), amount);
        piles[1] = new ItemPile(new TreeSeeds(treeType), rand.nextInt(5));
        return piles;
    }

    public void tick(World world, List<Entity> near) {
        super.tick(world, near);
//        if (GameComponent.debug.State()) {
//            kill();
//        }
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        int treeNum = 0;
        if (treeType == Type.Oak) treeNum = 0;
        if (treeType == Type.Pine) treeNum = 1;
        BufferedImage trunkFrame = trunk.getSprite(0).getStill(treeNum);
        BufferedImage topFrame = top.getSprite(treeNum).getStill(0);
        g.drawImage(trunkFrame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (trunkFrame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - trunkFrame.getHeight() + (2 * height)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        g.drawImage(topFrame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (topFrame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - topFrame.getHeight()) - treeHeight + (height / 2) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        super.render(g, xOffset, yOffset);
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return e.isFlying();
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return movementType == movementTypes.Flying;
    }
}