package com.tynellis.World.Tiles.LandTiles.ManMade;

import com.tynellis.Art.Sprite;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Humanoid;
import com.tynellis.World.Tiles.LandTiles.ConnectorTile;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

public class Ladder extends ConnectorTile {
    private static final SpriteSheet TOP = new SpriteSheet("tempArt/lpc/mine/ladder.png", 32, 64, 1);
    private static final double altPercent = 0.10;

    public Ladder(Random rand, int heightinWorld, int faceing, Tile base, int top, int bottom) {
        super("Ladder", null, rand, altPercent, base.getRank(), heightinWorld, base);
        direction = faceing % 4;
        height = top;
        this.bottom = bottom;
        setFull(true, false);
        base.startArt();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        getBase().startArt();
    }

    @Override
    public void render(Graphics g, int x, int y) {
        getBase().render(g, x, y);
    }


    @Override
    public void renderTop(Graphics g, int x, int y) {
        int yoffset = 0;
        int picNum = 0;
        Sprite image = TOP.getSprite(0);
        if (direction % 2 == 1) {
            picNum = 1;
            yoffset = 2 * Tile.HEIGHT / 4;
        } else if (direction == 0) {
            yoffset = Tile.HEIGHT;
        }
        if (direction > 1) {
            image.flipHoriz(true);
        } else {
            image.flipHoriz(false);
        }
        g.drawImage(image.getStill(picNum), x, y - yoffset, null);
    }

    @Override
    public void update(Tile[][] adjacent) {

    }

    @Override
    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove, boolean isOver) {
        if (bb instanceof Humanoid) {
            if (((Entity) bb).isWalking() && isOver) {
                double down = 0;
                if (direction % 2 == 0 && !((Entity) bb).getCanMoveY()) {
                    down = yMove;
                } else if ((direction == 1 || direction == 3) && !((Entity) bb).getCanMoveX()) {
                    down = xMove;
                }
                if (direction < 2) {
                    down = -down;
                }
                if (((Entity) bb).getZ() - down >= height) {
                    down = ((Entity) bb).getZ() - height;
                } else if (((Entity) bb).getZ() - down <= bottom) {
                    down = ((Entity) bb).getZ() - bottom;
                }
                ((Entity) bb).setFalling(down);
                if (!((Double) ((Entity) bb).getZ()).equals(height * 1.0) && !((Double) ((Entity) bb).getZ()).equals(bottom * 1.0)) {
                    ((Entity) bb).setCanMove(false, false);
                } else if (((Double) ((Entity) bb).getZ()).equals(height * 1.0)) {
                    if (direction % 2 == 0) {
                        ((Entity) bb).setCanMove(false, true);
                    } else {
                        ((Entity) bb).setCanMove(true, false);
                    }
                }
                if (!((Double) ((Entity) bb).getZ()).equals(bottom * 1.0)) {
                    ((Entity) bb).setLooking(direction);
                }

            }
        }
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Ladder(rand, height, direction, getBase(), this.height, bottom);
    }

    @Override
    public boolean canUse(Entity e) {
        return e.isWalking() && e instanceof Humanoid;
    }
}

