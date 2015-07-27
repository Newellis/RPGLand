package com.tynellis.World.Tiles;

import com.tynellis.Art.Sprite;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.Entities.BoundingBoxOwner;
import com.tynellis.World.Nodes.Node;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Random;

public abstract class Tile implements BoundingBoxOwner, Serializable {
    public final static int WIDTH = 32;
    public final static int HEIGHT = 32;
    public transient SpriteSheet top;
    private static final int adjacentNum = 9;
    private transient Sprite[] sprite;
    private int[] still;
    private int rank;
    private String name;
    private double altPercent;
    private final double alt;
    private boolean isObstructed = false;

    public Tile(String name, SpriteSheet sheet, Random rand, double altPercent, int rank) {
        this.name = name;
        top = sheet;
        this.rank = rank;
        alt = rand.nextDouble();
        this.altPercent = altPercent;
        startArt();
    }

    public void startArt(){
        still = new int[adjacentNum];
        sprite = new Sprite[adjacentNum];
        if (alt < altPercent){ //use alt art for tiles
            sprite[0] = top.getSprite(5);
            if (alt <= altPercent/20.0){
                still[0] = 0;
            } else if(alt <= altPercent/3.0) {
                still[0] = 1;
            } else {
                still[0] = 2;
            }
        } else {
            sprite[0] = top.getSprite(3);
            still[0] = 1;
        }
    }

    public void render(Graphics g, int x, int y) {
        for (int i = sprite.length-1; i >= 0; i-- ){
            if (sprite[i] != null) {
                g.drawImage(sprite[i].getStill(still[i]), x, y, null);
            }
        }
    }

    public void update(){
    }

    public void updateArt(Tile[][] adjacent){
        int[] ranks = new int[adjacentNum];
        java.util.Arrays.fill(ranks, -1);
        ranks[0] = this.rank;
        startArt();
        for (int i = 0; i < adjacent.length; i++) {
            for (int j = 0; j < adjacent[i].length; j++){
                int insertAt = -1;
                int corner = -1;
                Tile[] adjacentSides = new Tile[2];
                if (i == 1 && j == 1) {
                    continue;
                }
                if ((i == 0 || i == 2) && (j == 0 || j == 2)){
                    adjacentSides[0] = adjacent[i][1];
                    adjacentSides[1] = adjacent[1][j];
                }
                if (adjacentSides[1] != null && adjacentSides[0] != null && adjacent[i][j] != null) {
                    if (adjacent[i][j].getRank() == adjacentSides[0].getRank() || adjacent[i][j].getRank() == adjacentSides[1].getRank() ){
                        continue;
                    }
                }
                if ((j == 1 || i == 1) && corner > -1){
                    continue;
                }
                if (j == 1 && adjacent[i][j] != null ){
                    if (adjacent[1][0] != null && adjacent[i][j].getName().compareTo(adjacent[1][0].getName()) == 0) {
                        if (i == 2) {
                            corner = 1;
                        } else {
                            corner = 0;
                        }
                    } else if (adjacent[1][2] != null && adjacent[i][j].getName().compareTo(adjacent[1][2].getName()) == 0) {
                        if (i == 2) {
                            corner = 3;
                        } else {
                            corner = 2;
                        }
                    }
                }else if (i == 1 && adjacent[i][j] != null && adjacent[2][1] != null && adjacent[i][j].getName().compareTo(adjacent[2][1].getName()) == 0) {
                    if (j == 2) {
                        corner = 3;
                    } else {
                        corner = 1;
                    }
                }
                for (int k = adjacentNum - 1; k >= 0 ; k--){
                    if (ranks[k] == -1 || adjacent[i][j] == null || adjacent[i][j].getRank() == rank) {
                        continue;
                    }
                    if (adjacent[i][j].getRank() > ranks[k]){
                        break;
                    }
                    if (adjacent[i][j].getRank() == adjacent[i][j].getRank()){

                    }
                    if (adjacent[i][j].getRank() < ranks[k]){
                        insertAt = k;
                    }
                }
                if (insertAt == -1) {
                    continue;
                }
                for (int k = adjacentNum - 1; k > insertAt ; k--){
                    ranks[k] = ranks[k-1];
                    still[k] = still[k-1];
                    sprite[k] = sprite[k-1];
                }
                if (corner == -1) {
                    ranks[insertAt] = adjacent[i][j].getRank();
                    still[insertAt] = 2 - i;
                    sprite[insertAt] = adjacent[i][j].getSheet(this).getSprite(2 + (2 - j));
                } else {
                    ranks[insertAt] = adjacent[i][j].getRank();
                    still[insertAt] = 1 + corner % 2;
                    sprite[insertAt] = adjacent[i][j].getSheet(this).getSprite(corner/2);
                }
            }

        }

    }

    public int getRank(){
        return rank;
    }

    public SpriteSheet getSheet(Tile tile){
        return top;
    }

    public String getName() {
        return name;
    }

    public Rectangle getBounds(){
        return new Rectangle(WIDTH, HEIGHT);
    }

    public void handleCollision(BoundingBoxOwner bb, double xMove, double yMove){

    }
    public void setObstructed(boolean obstructed){
        isObstructed = obstructed;
    }
    public boolean isObstructed() {
        return isObstructed;
    }
}
