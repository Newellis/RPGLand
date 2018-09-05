package com.tynellis.debug.algs;

import com.tynellis.World.World;
import com.tynellis.World.world_parts.Regions.Generator.CaveGen;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaveGenTest extends AlgTest {

    private int[][] array;
    private BufferedImage map = new BufferedImage(Region.WIDTH, Region.HEIGHT, BufferedImage.TYPE_INT_ARGB);
    ;

    public CaveGenTest() {
        Name = "Cave Gen";
    }

    @Override
    public boolean Start() {
        this.setPreferredSize(new Dimension(Region.WIDTH, Region.HEIGHT));
        Random random = new Random();
        long seed = 2000000; //random.nextLong();
        CaveGen gen = new CaveGen(new World("test", seed));
//        array = gen.getCaveAreas();
        fillMap();
        return false;
    }

    private void fillMap() {
        Graphics2D g2 = (Graphics2D) map.getGraphics();
        int width = this.getHeight();
        double scale = 1;//width / (double) array.length;
        System.out.println("render, width: " + width);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[x].length; y++) {
                if (array[x][y] == 1) {
                    g2.setColor(Color.lightGray);
                } else {
                    g2.setColor(Color.BLACK);
                }
                g2.fillRect((int) Math.round(x * scale), (int) Math.round(y * scale), (int) Math.round((x + 1) * scale), (int) Math.round((y + 1) * scale));
            }
        }
    }

    @Override
    public boolean Step() {
        return false;
    }

    @Override
    protected void render(Graphics g) {
        g.drawImage(map, 0, 0, null);
    }
}
