package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.LandTiles.ConnectorTile;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class Slope extends ConnectorTile {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/lavarock.png", 32, 32, 1);
    private static final double altPercent = 0.10;

    public Slope(Random rand, int height, int direction, Tile base, double top, double bottom) {
        super("Slope", SHEET, rand, altPercent, base.getRank(), height, base);
        this.direction = direction % 4;
        this.height = top;
        this.bottom = bottom;
        setFull(true, false);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        getBase().startArt();
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    public void render(Graphics g, int x, int y) {

    }

    @Override
    public void renderTop(Graphics g, int x, int y) {
        getBase().render(g, x, y);
        if (direction == 0 || direction == 2) {
            g.drawImage(cliffEdge.getSprite(3).getStill(0), x, y, null);
            g.drawImage(cliffEdge.getSprite(3).getStill(2), x, y, null);
        } else {
            g.drawImage(cliffEdge.getSprite(2).getStill(1), x, y, null);
            g.drawImage(cliffEdge.getSprite(4).getStill(1), x, y, null);
        }
    }

    @Override
    public void update(Tile[][] adjacent) {
    }

    @Override
    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove, boolean isOver) {
//        if (bb instanceof Entity) {
//            if (((Entity) bb).isWalking()) {
//                double down = 0;
//                if (direction % 2 == 0 && ((Entity) bb).getCanMoveY()) {
//                    down = yMove;
//                } else if ((direction == 1 || direction == 3) && ((Entity) bb).getCanMoveX()){
//                    down = xMove;
//                }
//                if (direction < 2) {
//                    down = -down;
//                }
//                if (((Entity) bb).getZ() - down >= height) {
//                    down = ((Entity) bb).getZ() - height;
//                } else if (((Entity) bb).getZ() - down <= bottom) {
//                    down = ((Entity) bb).getZ() - bottom;
//                }
//                ((Entity) bb).setFalling(down);
//            }
//        }
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Slope(rand, height, direction, getBase(), this.height, bottom);
    }

    public boolean isPassableBy(Entity e) {
        if (e.getMovingDir() == direction * 2) {
            return !e.isSwimming();
        }
        return false;
    }

    @Override
    public double getHeightDeviationAt(double x, double y) {
        double height;
        if (direction % 2 == 0) {
            height = y % 1.0;
        } else {
            height = x % 1.0;
        }
        if (direction < 2) {
            height = 1 - height;
        }
        return height;
    }

    @Override
    public boolean canUse(Entity e) {
        return e.isWalking();
    }
}

