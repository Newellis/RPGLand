package com.tynellis.Entities;

import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Entity implements BoundingBoxOwner, Serializable {
    protected double speed = 0.06;
    protected boolean canBeMoved = true;
    protected transient boolean moving = false;
    protected int facing = 0;
    protected double facingAngle = 0;
    protected int spriteFacing = 0;
    private double xPushed, yPushed;
    private boolean xMoving, yMoving;
    protected int width;
    protected int height;
    protected double posX;
    protected double posY;
    protected int posZ;

    public enum movementTypes{
        Flying,
        Walking,
        Swimming,
    }
    protected movementTypes movementType = movementTypes.Walking;

    public Entity(double x, double y, int z, int width, int height) {
        posX = x;
        posY = y;
        posZ = z;
        this.width = width;
        this.height = height;
    }

    public void tick(World world){
        double xMove = 0;
        double yMove = 0;
        facingAngle = (Math.PI / 4 * facing);
        yPushed = xPushed = 0;
        yMoving = xMoving = canBeMoved;


        if (moving) {
            xMove += (Math.sin(facingAngle) * speed);
            yMove += (Math.cos(facingAngle) * speed);
        }

        if (canBeMoved) {
            ArrayList<Entity> near = world.getEntitiesNearEntity(this);
            Rectangle rect = getBounds();
            rect.setLocation((int)(rect.x + xMove * Tile.WIDTH), (int)(rect.y + yMove * Tile.HEIGHT));
            for (Entity entity: near){
                if (rect.intersects(entity.getBounds())){
                    handleCollision(entity, xMove, yMove);
                }
            }

            int moving;
            if (xPushed != 0 || yPushed != 0) {
                double movingAngle;
                if (Math.asin((xMove + xPushed) / speed) >= 0) {
                    movingAngle = Math.acos((yMove + yPushed) / speed);

                } else {
                    movingAngle = (2 * Math.PI - Math.acos((yMove + yPushed) / speed));
                    if (movingAngle == 3.926990816987241) { //fix for if moving should be 5
                        movingAngle += 0.0000000000000004;
                    }
                }
                moving = (int) (movingAngle / (Math.PI / 4));
            } else {
                moving = facing;
            }
            if (xMove + xPushed != 0 && xMoving && canMoveX(world, near, moving)) {
                posX -= (xMove + xPushed);
            }
            if (yMove + yPushed != 0 && yMoving && canMoveY(world, near, moving)){
                double lastPosY = posY;
                posY -= (yMove + yPushed);
                if (posY - lastPosY != 0.0) {
                   world.addMoveEntity(this);
                }
            }
        }
    }

    public void render(Graphics g, int xOffset, int yOffset){
        if (World.DEBUG) {
            Rectangle rectangle = getBounds();
            g.setColor(Color.BLUE);
            g.drawRect(rectangle.x + xOffset, rectangle.y + yOffset, rectangle.width, rectangle.height);
        }
    }

    public double getX() {
        return posX;
    }

    public double getY() {
        return posY;
    }

    public int getZ() {
        return posZ;
    }

    public Rectangle getBounds() {
        return new Rectangle((int)((posX + 0.5) * Tile.WIDTH) - (width / 2), (int)((posY + 0.5) * Tile.HEIGHT) - (height), width, height);
    }

    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove){
        if (!bb.isPassableBy(this)){
            Rectangle bounds = getBounds(), next = getBounds();
            next.setLocation(next.x - (int)(2 * xMove * Tile.WIDTH), next.y - (int)(2 * yMove * Tile.HEIGHT));

            if (bb instanceof Entity){
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


    public boolean canMoveX(World world, ArrayList<Entity> near, int direction){
        boolean canMove = true;
        Rectangle rect = getBounds();
        int y1 = (int)(posY - .9);
        int y2 = (int)(posY + ((rect.height * 1.0)/Tile.HEIGHT) - .6);
        if (direction != 0 && direction != 4){
            for (int y = y1; y <= y2; y++) {
                if (direction < 4) {
                    int x = (int)posX;
                    canMove &= !tileBlocks(world, rect, x, y);
                } else if (direction < 8) {
                    int x = (int)(posX + (rect.width / Tile.WIDTH));
                    canMove &= !tileBlocks(world, rect, x, y);
                }
            }
        }
        return canMove;
    }

    public boolean canMoveY(World world, ArrayList<Entity> near, int direction){
        boolean canMove = true;
        Rectangle rect = getBounds();
        int x1 = (int)(posX + .1);
        int x2 = (int)(posX + ((rect.width * 1.0) / Tile.WIDTH) - .1);
        if (direction != 2 && direction != 6){
            for (int x = x1; x <= x2; x++) {
                if (direction < 2 || direction > 6) {
                    int y = (int) posY - 1;
                    canMove &= !tileBlocks(world, rect, x, y);
                } else if (direction < 6) {
                    int y = (int)(posY + (rect.height / Tile.HEIGHT));
                    canMove &= !tileBlocks(world, rect, x, y);
                }
            }
        }
        return canMove;
    }

    private boolean tileBlocks(World world, Rectangle rect, int x, int y){
        Tile downTile = world.getTile(x, y, posZ);
        if (downTile != null && !downTile.isPassableBy(this) && rect.intersects(world.getTileBounds(x, y, (int) posZ))) {
            return true;
        } else if (downTile == null) {
            return true;
        }
        return false;
    }

    private boolean entitiesBlock(ArrayList<Entity> near, Rectangle rect) {
        boolean notBlocks = true;
        for (Entity entity: near) {
            notBlocks &= !entity.getBounds().intersects(rect);
        }
        return !notBlocks;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(double facing) {
        spriteFacing = (int)facing;
        this.facing = (int)(facing * 2.0);
    }

    @Override
    public String toString() {
        return "Entity{" + getClass() +
                ", posY= " + posY +
                ", posX= " + posX +
                '}';
    }

    public void setMoving(boolean moving){
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
}
