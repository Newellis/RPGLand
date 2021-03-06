package com.tynellis;

import com.tynellis.World.World;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Regions.Generator.SurfaceGen;
import com.tynellis.World.world_parts.Regions.Region;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.Random;

public class WorldComponent extends JComponent{
    private int[][] array;
    private int[] spawn = new int[3];
    public static void main(String[] args) {
        WorldComponent mc = new WorldComponent();
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(mc);
        frame.setContentPane(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public WorldComponent() {
        this.setPreferredSize(new Dimension(Region.WIDTH, Region.HEIGHT));
        Random random = new Random();
        long seed = random.nextLong();
        seed = 2000000;
        System.out.println("Seed: " + seed);
        World world = new World("test", seed);
        world.genSpawn(seed);
        spawn = world.getSpawnPoint();
        array = world.gen.getLandAreas(); //region.gen.erodeArea(20 * Area.WIDTH, 10 * Area.HEIGHT, new Random(seed * ((20 * Area.WIDTH * Region.WIDTH) + 10 * Area.HEIGHT)));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[x].length; y++) {
                if (array[x][y] > SurfaceGen.SNOW_LEVEL) {
                    g2.setColor(new Color(0.95f, 0.95f, 1.0f));
                } else if (array[x][y] > SurfaceGen.TREE_LEVEL) {
                    g2.setColor(new Color(0.0f, 0.5f, 0.0f));
                } else if (array[x][y] > SurfaceGen.SLOPE_LEVEL) {
                    g2.setColor(new Color(0.0f,0.6f,0.0f));
                } else if (array[x][y] > SurfaceGen.MOUNTAIN_BASE_LEVEL) {
                    g2.setColor(new Color(0.0f,0.7f,0.0f));
                } else if (array[x][y] > SurfaceGen.HILL_LEVEL) {
                    g2.setColor(new Color(0.0f,0.8f,0.0f));
                } else if (array[x][y] > SurfaceGen.BEACH_MAX_LEVEL) {
                    g2.setColor(new Color(0.0f,0.9f,0.0f));
                } else if (array[x][y] > SurfaceGen.SEA_LEVEL) {
                    g2.setColor(new Color(0.9019608f, 0.8980392f, 0.1882353f));
                } else if (array[x][y] > 23){
                    g2.setColor(new Color(0.5f,0.5f,1.0f));
                } else {
                    g2.setColor(new Color(0.0f, 0.0f, 1.0f));
                }
                //g2.setColor(new Color((int)(((array[x][y]-50.0)/100)*255), (int)(((array[x][y]-50.0)/100)*255), (int)(((array[x][y]-50.0)/100)*255)));
                g2.draw(new Line2D.Double(x, y, x+1, y+1));
            }
            //System.out.printf("%3.1f Done\n",((x + 1.0)/Region.WIDTH) * 100.0);
        }
        g2.setColor(Color.RED);
        g2.fillRect(spawn[0] / Area.WIDTH - 2, spawn[1] / Area.HEIGHT - 2, 5, 5);
    }
}
