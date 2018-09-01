package com.tynellis.World.Buildings;

import com.tynellis.Art.Sprite;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class SmallHouse extends Building {

    protected static transient SpriteSheet wallSheet = new SpriteSheet("tempArt/lpc/buildings/cottage.png", 32, 32, 1);
    protected static transient SpriteSheet roofSheet = new SpriteSheet("tempArt/lpc/buildings/thatched-roof.png", 32, 32, 1);
    protected static transient SpriteSheet doorSheet = new SpriteSheet("tempArt/lpc/buildings/doors.png", 64, 64, 1);
    protected static transient SpriteSheet windowSheet = new SpriteSheet("tempArt/lpc/buildings/windows.png", 32, 32, 1);

    private int foundationType;
    private int foundationStyle;
    private int wallType;
    private boolean wallReinforcements;
    private int roofColor;
    private int doorLocation;
    private ArrayList<Integer> windowLocations;

    public SmallHouse(double x, double y, double z, int width, int height, Random random) {
        super(x, y, z, width * Tile.WIDTH, height * Tile.HEIGHT);
        foundationType = wallType = random.nextInt(3);
        if (random.nextBoolean()) {
            foundationType = 2;
        }
        wallReinforcements = wallType != 2 && random.nextBoolean();
        foundationStyle = random.nextInt(2);
        roofColor = random.nextInt(2);
        if (width >= 3) {
            doorLocation = 1 + random.nextInt(width - 2);
        }
        int leftSpaces = doorLocation;
        int rightSpaces = width - doorLocation - 1;
        windowLocations = new ArrayList<Integer>();
        if (rightSpaces == leftSpaces && width > 3 && width < 10) {
            windowLocations.add((leftSpaces / 2));
            windowLocations.add((width - 1) - (rightSpaces / 2));
        } else {
            if (rightSpaces > 4) {
                windowLocations.add((width - 1) - (int) (rightSpaces / 3.0));
                windowLocations.add((width - 1) - (int) (2 * (rightSpaces / 3.0)));
            } else if (rightSpaces >= 3) {
                windowLocations.add((width - 1) - (rightSpaces / 2));
            }
            if (leftSpaces > 4) {
                windowLocations.add((int) (leftSpaces / 3.0));
                windowLocations.add((int) (2 * (leftSpaces / 3.0)));
            } else if (leftSpaces >= 3) {
                windowLocations.add((leftSpaces / 2));
            }
        }
        for (Integer num : windowLocations) {
            System.out.println("Window space " + num);
        }
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }

    @Override
    public void render(Graphics g, int xOffset, int yOffset) {

        int lowerRightX = (int) (((posX + 0.5) * Tile.WIDTH) + xOffset - (width / 2.0));
        int lowerRightY = (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - Tile.HEIGHT);
        Sprite foundationTypeSprite = wallSheet.getSprite((foundationType * 4) + 2);

        for (int i = 0; i < width / Tile.WIDTH; i++) {
            int section = 1;
            if (i == 0) section = 0;
            if (i == (width / Tile.WIDTH) - 1) section = 2;
            BufferedImage foundation = foundationTypeSprite.getStill((foundationStyle * 3) + section);
            g.drawImage(foundation, lowerRightX + (i * Tile.WIDTH), lowerRightY, null);
            if (section != 1 && wallReinforcements) section = 3;
            if (i == doorLocation) section += 9;
            for (Integer windowLocation : windowLocations) {
                if (i == windowLocation) section += 9;
            }
            for (int j = 0; j < 2; j++) {
                Sprite wallTypeSprite = wallSheet.getSprite((wallType * 4) + (1 - j));
                wallTypeSprite.flipHoriz(i == (width / Tile.WIDTH) - 1 && wallReinforcements);
                BufferedImage wall = wallTypeSprite.getStill(section);
                g.drawImage(wall, lowerRightX + (i * Tile.WIDTH), lowerRightY - (j * Tile.HEIGHT) - Tile.HEIGHT, null);
            }
        }
        g.drawImage(wallSheet.getSprite(15).getStill(7), lowerRightX + (doorLocation * Tile.WIDTH), lowerRightY, null);
        Sprite doorSprite = doorSheet.getSprite(4);
        BufferedImage door = doorSprite.getStill(0);
        g.drawImage(door, lowerRightX + (doorLocation * Tile.WIDTH) - (Tile.WIDTH / 2) - 1, lowerRightY - Tile.HEIGHT, null);

        for (Integer windowLocation : windowLocations) {
            for (int i = 0; i < 3; i++) {
                Sprite windowSprite = windowSheet.getSprite(i);
                BufferedImage window = windowSprite.getStill(10);
                g.drawImage(window, lowerRightX + (windowLocation * Tile.WIDTH), lowerRightY + (i * Tile.HEIGHT) - (int) (2.5 * Tile.HEIGHT), null);
            }
        }

        drawRoofEdge(g, lowerRightX, lowerRightY, width / Tile.WIDTH, height / Tile.HEIGHT, roofColor);
        drawRoof(g, lowerRightX, lowerRightY, width / Tile.WIDTH, height / Tile.HEIGHT, 3, roofColor);

        super.render(g, xOffset, yOffset);
    }

    private void drawRoofEdge(Graphics g, int lowerRightX, int lowerRightY, int width, int height, int roofColor) {
        for (int i = 0; i < width + 2; i++) {
            int section = 1;
            if (i == 0) section = -1;
            if (i == width + 1) section = 3;
            for (int j = -1; j < height; j++) {
                int ySection = 1;
                if (j == height - 1 && section != 1) ySection = 0;
                if (j == -1) ySection = 3;
                if (!(section == 1 && ySection == 1)) {
                    Sprite roofSprite = roofSheet.getSprite((roofColor * 7) + (ySection));
                    BufferedImage roof = roofSprite.getStill(section + 3);
                    g.drawImage(roof, lowerRightX + (i * Tile.WIDTH) - Tile.WIDTH, lowerRightY - (j * Tile.HEIGHT) - (3 * Tile.HEIGHT), null);
                }
            }
        }
    }

    private static void drawRoof(Graphics g, int lowerRightX, int lowerRightY, int width, int height, int roofSlope, int roofColor) {
        for (int i = 0; i < width; i++) {
            int section = 1;
            if (i == 0) section = 0;
            if (i == width - 1) section = 2;
            if (width == 1) section = 1;

            for (int j = 0; j < height; j++) {
                int ySection = 1;
                if (j == height - 1 && section != 1) ySection = 0;
                if (j == 0) ySection = 2;
                if (width == 1) {
                    ySection--;
                    if (j == height - 1) {
                        ySection = 5;
                        section = 2 - roofSlope;
                        if (section < 0) section += 3;
                        if (roofSlope == 1) ySection = 4;
                    }
                    if (roofSlope == 1 && j == height - 2) {
                        ySection = 5;
                        section = 1;
                    }
                }
                if (roofSlope != 3 && ySection == 0 || roofSlope == 1 && j == height - 2 && (height - 2 > 0)) {
                    if (roofSlope == 1 && width > 1) {
                        ySection += 2;
                    }
                    if (section == 0) section -= 2;
                    if (section == 2) section += 2;
                }
                if (!(section == 1 && ySection == 1) || width == 1) {
                    Sprite roofSprite = roofSheet.getSprite((roofColor * 7) + (ySection));
                    BufferedImage roof = roofSprite.getStill(section + 3);
                    g.drawImage(roof, lowerRightX + (i * Tile.WIDTH), lowerRightY - (j * Tile.HEIGHT) - (3 * Tile.HEIGHT), null);
                }
            }
        }
        if (roofSlope != 3) {
            height--;
        }
        roofSlope++;
        if (roofSlope > 3) {
            roofSlope = 1;
        }

        if (width >= 3) {
            drawRoof(g, lowerRightX + Tile.WIDTH, lowerRightY - Tile.WIDTH, width - 2, height, roofSlope, roofColor);
        }
    }
}
