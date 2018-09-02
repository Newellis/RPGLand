package com.tynellis.debug.algs;

import com.tynellis.World.World;
import com.tynellis.World.WorldGen;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Region;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

public class WorldGenTest extends AlgTest {

    private int[][] array;
    private int[] spawn;

    public WorldGenTest() {
        Name = "Region Gen";
    }

    @Override
    public boolean Start() {
        this.setPreferredSize(new Dimension(Region.WIDTH, Region.HEIGHT));
        Random random = new Random();
        long seed = 2000000; //random.nextLong();
        System.out.println("Seed: " + seed);
        World world = new World("test", seed);
//        Region world = new Region("test", seed);
        world.genSpawn(seed);
        spawn = world.getSpawnPoint();
        array = world.gen.getLandAreas(); //world.gen.erodeArea(20 * Area.WIDTH, 10 * Area.HEIGHT, new Random(seed * ((20 * Area.WIDTH * Region.WIDTH) + 10 * Area.HEIGHT)));
        return true;
    }

    @Override
    public boolean Step() {
        return false;
    }

    @Override
    protected void render(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getHeight();
        double scale = width / (double) array.length;
        System.out.println("render, width: " + width);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[x].length; y++) {
                if (array[x][y] > WorldGen.SNOW_LEVEL) {
                    g2.setColor(new Color(0.95f, 0.95f, 1.0f));
                } else if (array[x][y] > WorldGen.TREE_LEVEL) {
                    g2.setColor(new Color(0.0f, 0.5f, 0.0f));
                } else if (array[x][y] > WorldGen.SLOPE_LEVEL) {
                    g2.setColor(new Color(0.0f, 0.6f, 0.0f));
                } else if (array[x][y] > WorldGen.MOUNTAIN_BASE_LEVEL) {
                    g2.setColor(new Color(0.0f, 0.7f, 0.0f));
                } else if (array[x][y] > WorldGen.HILL_LEVEL) {
                    g2.setColor(new Color(0.0f, 0.8f, 0.0f));
                } else if (array[x][y] > WorldGen.BEACH_MAX_LEVEL) {
                    g2.setColor(new Color(0.0f, 0.9f, 0.0f));
                } else if (array[x][y] > WorldGen.SEA_LEVEL) {
                    g2.setColor(new Color(0.9019608f, 0.8980392f, 0.1882353f));
                } else if (array[x][y] > 23) {
                    g2.setColor(new Color(0.5f, 0.5f, 1.0f));
                } else {
                    g2.setColor(new Color(0.0f, 0.0f, 1.0f));
                }
                //g2.setColor(new Color((int)(((array[x][y]-50.0)/100)*255), (int)(((array[x][y]-50.0)/100)*255), (int)(((array[x][y]-50.0)/100)*255)));
                //g2.draw(new Rectangle2D.Double(x * scale, y * scale, (x+1) * scale, (y+1) * scale));
                g2.fillRect((int) Math.round(x * scale), (int) Math.round(y * scale), (int) Math.round((x + 1) * scale), (int) Math.round((y + 1) * scale));

            }
            //System.out.printf("%3.1f Done\n",((x + 1.0)/Region.WIDTH) * 100.0);
        }
        g2.setColor(Color.RED);
        g2.fillRect((int) ((spawn[0] / Area.WIDTH - 2) * scale), (int) ((spawn[1] / Area.HEIGHT - 2) * scale), 5, 5);

    }
}
