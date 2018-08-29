package com.tynellis.World.Entities;

import com.tynellis.BoundingBox.BoundingBoxOwner;
import com.tynellis.GameComponent;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Light.LightSource;
import com.tynellis.World.Tiles.LandTiles.LayeredTile;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.debug.Debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Entity implements BoundingBoxOwner, Serializable {
    protected double speed = 0.06;
    protected boolean canBeMoved = true;
    protected boolean moving = false;
    protected int facing = 0;
    protected double facingAngle = 0;
    protected int spriteFacing = 0;
    private double xPushed, yPushed, zFalling;
    private boolean xMoving, yMoving;
    private int movingDir;
    protected int width;
    protected int height;
    protected double posX, posY, posZ;
    protected boolean isDead = false;
    protected LightSource light;


    public void doDrops(World world) {
        ItemPile[] items = getItemsToDrop(world.getRand());
        for (ItemPile pile : items) {
            if (pile != null && pile.getSize() > 0) {
                world.addEntity(new ItemEntity(pile, world.getRand(), posX - 0.25 + (world.getRand().nextDouble() / 2), posY - 0.25 + (world.getRand().nextDouble() / 2), posZ));
            }
        }
    }

    public abstract ItemPile[] getItemsToDrop(Random rand);

    public void performDeath(World world) {
        doDrops(world);
    }

    public abstract int compareTo(Entity entity);//add comparison for how an entity should be compared to others of the same type

    public enum movementTypes {
        Flying,
        Walking,
        Swimming,
    }

    protected movementTypes movementType = movementTypes.Walking;

    public Entity(double x, double y, double z, int width, int height) {
        posX = x;
        posY = y;
        posZ = z;
        this.width = width;
        this.height = height;
    }

    public void tick(World world, List<Entity> near) {
        facingAngle = (Math.PI / 4 * facing);
        yPushed = xPushed = zFalling = 0;
        yMoving = xMoving = canBeMoved;


        move(world, speed, near);
    }

    private void move(World world, double speed, List<Entity> near) {
        double xMove = 0, yMove = 0;
        double[] centerPoint = isOverPoint();
        Rectangle rect = getBounds();
        rect.setLocation((int) (rect.x + xMove * Tile.WIDTH), (int) (rect.y + yMove * Tile.HEIGHT));

        if (moving) {
            xMove += (Math.sin(facingAngle) * speed);
            yMove += (Math.cos(facingAngle) * speed);
        }
        for (Entity entity : near) {
            if (rect.intersects(entity.getBounds()) && posZ - zFalling == entity.getZ()) {
                handleCollision(entity, xMove, yMove, entity.getBounds().contains(centerPoint[0], centerPoint[1]));
            }
        }
        if (canBeMoved) {
            if (movementType == movementTypes.Walking) {
                double groundLevel = world.getTileHeightAt(centerPoint[0] + xMove, centerPoint[1] + yMove, centerPoint[2] - zFalling);
                //System.out.println(groundLevel + ": player " + posZ);
//                if (groundLevel < posZ - zFalling) {
//                    zFalling += 0.1;
//                } else
                if (groundLevel != posZ - zFalling) {
                    if (groundLevel - (posZ - zFalling) <= 1 / 4) {
                        zFalling = groundLevel - (posZ - zFalling);
                    }
                }
            }

            if (xPushed != 0 || yPushed != 0) {
                double movingAngle;
                if (Math.asin((xMove + xPushed) / speed) >= 0) {
                    movingAngle = Math.acos((yMove + yPushed) / speed);

                } else {
                    movingAngle = (2 * Math.PI - Math.acos((yMove + yPushed) / speed));
                    if (movingAngle == 3.926990816987241) { //fix for if movingDir should be 5
                        movingAngle += 0.0000000000000004;
                    }
                }
                movingDir = (int) (movingAngle / (Math.PI / 4));
            } else {
                movingDir = facing;
            }
            setCanMove(canMoveX(world, movingDir, xMove, yMove), canMoveY(world, movingDir, xMove, yMove));
            collideWithTiles(world, rect, xMove, yMove);
            if (!(((Double) (xMove + xPushed)).equals(0.0)) && xMoving) {
                posX -= (xMove + xPushed);
            }
            if (!(((Double) (yMove + yPushed)).equals(0.0)) && yMoving) {
                double lastPosY = posY;
                posY -= (yMove + yPushed);
                if (posY - lastPosY != 0.0) {
                    world.addMoveEntity(this);
                }
            }
            if (!(((Double) zFalling).equals(0.0))) {
                posZ -= zFalling;
            }
            posX = (double) Math.round(posX * 1000000d) / 1000000d;
            posY = (double) Math.round(posY * 1000000d) / 1000000d;
        }
    }

    private void collideWithTiles(World world, Rectangle rect, double xMove, double yMove) {
        rect.setSize(width * 2 / 3, height * 2 / 3);
        rect.setLocation((int) (rect.getX() + width / 6), (int) (rect.getY() + height / 6));
        ArrayList<Tile> downTiles = world.getTilesIntersectingRect(rect, (int) Math.floor(getZ()));
        for (Tile tile : downTiles) {
            if (tile != null) {
                double[] point = isOverPoint();
                tile.handleCollision(this, xMove, yMove, tile.equals(world.getTile((int) point[0], (int) point[1], (int) point[2])));
            }
        }
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        if (GameComponent.debug.State()) {
            if (GameComponent.debug.isType(Debug.Type.COLLISION)) {
                Rectangle rectangle = getBounds();
                g.setColor(Color.BLUE);
                g.drawRect(rectangle.x + xOffset, rectangle.y + yOffset, rectangle.width, rectangle.height);

//                Rectangle rect = getBounds();
//                int y1 = (int) Math.floor(posY + .1);
//                int y2 = (int) Math.floor(posY + (rect.height / Tile.HEIGHT) - .1);
//                int x1 = (int) Math.floor(posX + .1);
//                int x2 = (int) Math.floor(posX + (rect.width / Tile.WIDTH) - .1);
//                g.setColor(Color.RED);
//                if (facing != 0 && facing != 4) {
//                    for (int y = y1; y <= y2; y++) {
//                        if (facing < 4) {
//                            int x = (int) posX;
//                            Rectangle rect1 = new Rectangle(0, 0, Tile.WIDTH, Tile.HEIGHT);
//                            g.drawRect((x * Tile.WIDTH) + xOffset, (y * Tile.HEIGHT) + yOffset - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), rect1.width, rect1.height);
//                        } else if (facing < 8) {
//                            int x = (int) (posX + (rect.width / Tile.WIDTH));
//                            Rectangle rect1 = new Rectangle(0, 0, Tile.WIDTH, Tile.HEIGHT);
//                            g.drawRect((x * Tile.WIDTH) + xOffset, (y * Tile.HEIGHT) + yOffset - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), rect1.width, rect1.height);
//                        }
//                    }
//                }
//                g.setColor(Color.CYAN);
//                if (facing != 2 && facing != 6) {
//                    for (int x = x1; x <= x2; x++) {
//                        if (facing < 2 || facing > 6) {
//                            int y = (int) posY;
//                            Rectangle rect1 = new Rectangle(0, 0, Tile.WIDTH, Tile.HEIGHT);
//                            g.drawRect((x * Tile.WIDTH) + xOffset, (y * Tile.HEIGHT) + yOffset - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), rect1.width, rect1.height);
//                        } else if (facing < 6) {
//                            int y = (int) (posY + (rect.height / Tile.HEIGHT));
//                            Rectangle rect1 = new Rectangle(0, 0, Tile.WIDTH, Tile.HEIGHT);
//                            g.drawRect((x * Tile.WIDTH) + xOffset, (y * Tile.HEIGHT) + yOffset - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), rect1.width, rect1.height);
//                        }
//                    }
//                }
            }
        }
    }

    public double getX() {
        return posX;
    }

    public double getY() {
        return posY;
    }

    public double getZ() {
        return posZ;
    }

    public double getSpeed() {
        return speed;
    }

    public void setLocation(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) ((posX + 0.5) * Tile.WIDTH) - (width / 2), (int) ((posY + 0.5) * Tile.HEIGHT) - (height / 2) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), width, height);
    }

    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove, boolean isOver) {
        if (!bb.isPassableBy(this)) {
            Rectangle bounds = getBounds(), next = getBounds();
            next.setLocation(next.x - (int) (2 * xMove * Tile.WIDTH), next.y - (int) (2 * yMove * Tile.HEIGHT));

            if (bb instanceof Entity) {
                Entity entity = (Entity) bb;
                //push this by bb speed or block if entity.canBeMoved
                if (canBeMoved) {
                    if (bounds.intersects(entity.getBounds())) {
                        Rectangle rectangle = bounds.intersection(entity.getBounds()); //todo make not pull
                        if (rectangle.width < rectangle.height && rectangle.height > entity.height / 3 && next.intersection(entity.getBounds()).width >= rectangle.width) {
                            if (entity.canBeMoved) {
                                double xMoveEntity = (Math.sin(entity.facingAngle) * entity.speed);
                                xPushed += xMoveEntity;
                            } else {
                                xMoving = false;
                            }
                        }
                        if (rectangle.width > rectangle.height && rectangle.width > entity.width / 3 && next.intersection(entity.getBounds()).height >= rectangle.height) {
                            if (entity.canBeMoved) {
                                double yMoveEntity = (Math.cos(entity.facingAngle) * entity.speed);
                                yPushed += yMoveEntity;
                            } else {
                                yMoving = false;
                            }
                        }
                    }
                }
            }
        }
    }

    public abstract boolean isPassableBy(Entity e);


    private boolean canMoveX(World world, int direction, double xMove, double yMove) {
        boolean canMove = true;
        Rectangle rect = getBounds();
        int y1 = (int) Math.floor(posY + .1);
        int y2 = (int) Math.floor(posY + (rect.height / Tile.HEIGHT) - .1);
        if (direction != 0 && direction != 4) {
            for (int y = y1; y <= y2; y++) {
                if (direction < 4) {
                    int x = (int) posX;
                    canMove &= !tileBlocks(world, rect, x, y, xMove, yMove);
                } else if (direction < 8) {
                    int x = (int) (posX + (rect.width / Tile.WIDTH));
                    canMove &= !tileBlocks(world, rect, x, y, xMove, yMove);
                }
            }
        }
        return canMove;
    }

    private boolean canMoveY(World world, int direction, double xMove, double yMove) {
        boolean canMove = true;
        Rectangle rect = getBounds();
        int x1 = (int) Math.floor(posX + .1);
        int x2 = (int) Math.floor(posX + (rect.width / Tile.WIDTH) - .1);
        rect = new Rectangle(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2);
        if (direction != 2 && direction != 6) {
            for (int x = x1; x <= x2; x++) {
                if (direction < 2 || direction > 6) {
                    int y = (int) posY;
                    canMove &= !tileBlocks(world, rect, x, y, xMove, yMove);
                } else if (direction < 6) {
                    int y = (int) (posY + (rect.height / Tile.HEIGHT));
                    canMove &= !tileBlocks(world, rect, x, y, xMove, yMove);
                }
            }
        }
        return canMove;
    }

    private boolean tileBlocks(World world, Rectangle rect, int x, int y, double xMove, double yMove) {
        Tile downTile = world.getTile(x, y, (int) posZ);
        Tile upTile = world.getTile(x, y, (int) posZ + 1);
        Rectangle next = (Rectangle) rect.clone();
        next.setLocation(next.x - (int) (2 * xMove * Tile.WIDTH), next.y - (int) (2 * yMove * Tile.HEIGHT));
        if (upTile != null && !(upTile instanceof LayeredTile && !((LayeredTile) upTile).isBlocked())) {
            posX = (double) Math.round(posX * 100d) / 100d;
            posY = (double) Math.round(posY * 100d) / 100d;
            posZ = (double) Math.round(posZ * 100d) / 100d;
            return true;
        } else if (!((Double) (posZ - (int) posZ)).equals(0.0) && upTile == null) {
            posX = (double) Math.round(posX * 100d) / 100d;
            posY = (double) Math.round(posY * 100d) / 100d;
            posZ = (double) Math.round(posZ * 100d) / 100d;
            return true;
        } else if (downTile != null && !downTile.isPassableBy(this) && next.intersects(world.getTileBounds(x, y, (int) posZ))) {
            posX = (double) Math.round(posX * 100d) / 100d;
            posY = (double) Math.round(posY * 100d) / 100d;
            posZ = (double) Math.round(posZ * 100d) / 100d;
            return true;
        } else if (downTile == null) {
            Tile belowTile = world.getTile(x, y, (int) posZ - 1);
            if ((belowTile != null && belowTile instanceof LayeredTile && ((LayeredTile) belowTile).isFull())) {
                return false;
            }
            return !isFlying();
        }
        return false;
    }

    private boolean entitiesBlock(ArrayList<Entity> near, Rectangle rect) {
        boolean notBlocks = true;
        for (Entity entity : near) {
            notBlocks &= !entity.getBounds().intersects(rect);
        }
        return !notBlocks;
    }

    public void kill() {
        isDead = true;
    }

    public boolean isDead() {
        return isDead;
    }

    public int getFacing() {
        return facing;
    }

    public double getFacingAngle() {
        return facingAngle;
    }

    public void setFacing(double facing) {
        this.facing = (int) (facing * 2.0);
        if (this.facing == 0) spriteFacing = 0;
        else if (this.facing > 0 && this.facing < 4) spriteFacing = 1;
        else if (this.facing == 4) spriteFacing = 2;
        else if (this.facing > 4 && this.facing < 8) spriteFacing = 3;
    }

    public void setLooking(double looking) {
        spriteFacing = (int) looking;
        this.facing = (int) looking * 2;
    }

    @Override
    public String toString() {
        return "Entity{" + getClass().getName() +
                ", posY= " + posY +
                ", posX= " + posX +
                ", posZ= " + posZ +
                '}';
    }

    public void setFalling(double falling) {
        zFalling = falling;
    }

    public void setCanMove(boolean x, boolean y) {
        xMoving = x && xMoving;
        yMoving = y && yMoving;
    }

    public boolean getCanMoveX() {
        return xMoving;
    }

    public boolean getCanMoveY() {
        return yMoving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isFlying() {
        return movementType == movementTypes.Flying;
    }

    public boolean isWalking() {
        return movementType == movementTypes.Walking;
    }

    public boolean isSwimming() {
        return movementType == movementTypes.Swimming;
    }

    public double[] isOverPoint() {
        double cords[] = {posX + ((width / 2.0) / Tile.WIDTH), posY + ((height / 2.0) / Tile.HEIGHT), posZ};
        return cords;
    }

    public int getMovingDir() {
        return movingDir;
    }

    public boolean isMoveable() {
        return canBeMoved;
    }

    public LightSource getLight() {
        return light;
    }

}
