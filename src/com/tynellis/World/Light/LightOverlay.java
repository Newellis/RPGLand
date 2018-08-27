package com.tynellis.World.Light;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class LightOverlay {


    public void render(Graphics g, int width, int height, int xPos, int yPos, int zPos) {
        BufferedImage lightOverlay = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics overlay = lightOverlay.getGraphics();

        overlay.setColor(new Color(18, 45, 88, 255 - 90));
        overlay.fillRect(0, 0, width, height);

        //add all light sources

        
        //invert alpha
//        DataBufferInt buf = (DataBufferInt) lightOverlay.getRaster().getDataBuffer();
//        int[] values = buf.getData();
//        for(int i = 0; i < values.length; i += 4) values[i] = (values[i] ^ 0xff);

        //draw overlay
//        g.drawImage(lightOverlay, 0, 0, null);
    }

    private Color calculateColorAt(int x, int y) {
        return new Color(18, 45, 88, 90);
    }
}
