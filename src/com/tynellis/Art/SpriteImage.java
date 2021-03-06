package com.tynellis.Art;

import com.tynellis.GameComponent;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class SpriteImage {

    protected static BufferedImage[] slice(String path, int width, int scale) {
        BufferedImage origImage;
        try {
            origImage = ImageIO.read(GameComponent.class.getResource(path));
            BufferedImage[] images = new BufferedImage[origImage.getWidth() / width];
            for (int w = 0; w < images.length; w++) {
                BufferedImage slicedImage = new BufferedImage(width * scale, origImage.getHeight() * scale, BufferedImage.TYPE_INT_ARGB);
                slicedImage.getGraphics().drawImage(origImage, 0, 0, slicedImage.getWidth(), slicedImage.getHeight(), w * width, 0, (w + 1) * width, origImage.getHeight(), null);
                images[w] = slicedImage;
            }
            return images;
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    protected static BufferedImage[][] slice(String path, int width, int height, int scale) {
        return slice(path, width, height, 0, 0, scale);
    }

    protected static BufferedImage[][] slice(String path, int width, int height, int xOffset, int yOffset, int scale) {
        BufferedImage origImage;
        try {
            origImage = ImageIO.read(GameComponent.class.getResource(path));
            BufferedImage[][] images = new BufferedImage[origImage.getHeight() / height][origImage.getWidth() / width];
            for (int h = 0; h < images.length; h++) {
                for (int w = 0; w < images[h].length; w++) {
                    BufferedImage slicedImage = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_ARGB);
                    slicedImage.getGraphics().drawImage(origImage, 0, 0, slicedImage.getWidth(), slicedImage.getHeight(), (w * width) + xOffset, (h * height) + yOffset, ((w + 1) * width) + xOffset, ((h + 1) * height) + yOffset, null);
                    images[h][w] = slicedImage;
                }
            }
            return images;
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    protected static BufferedImage load(String path, int scale) {
        BufferedImage origImage;
        try {
            origImage = ImageIO.read(GameComponent.class.getResource(path));
            BufferedImage image = new BufferedImage(origImage.getWidth() * scale, origImage.getHeight() * scale, BufferedImage.TYPE_INT_ARGB);
            image.getGraphics().drawImage(origImage, 0, 0, origImage.getWidth() * scale, origImage.getHeight() * scale, null);
            return image;
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }

    public static BufferedImage Tint(BufferedImage image, Color color) {
        image.getRaster();
        BufferedImage tinted = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = tinted.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        for (int i = 0; i < tinted.getWidth(); i++) {
            for (int j = 0; j < tinted.getHeight(); j++) {
                int ax = tinted.getColorModel().getAlpha(tinted.getRaster().getDataElements(i, j, null));
                int rx = tinted.getColorModel().getRed(tinted.getRaster().getDataElements(i, j, null));
                int gx = tinted.getColorModel().getGreen(tinted.getRaster().getDataElements(i, j, null));
                int bx = tinted.getColorModel().getBlue(tinted.getRaster().getDataElements(i, j, null));
                rx = (color.getRed() + rx) / 2;
                gx = (color.getGreen() + gx) / 2;
                bx = (color.getBlue() + bx) / 2;
                tinted.setRGB(i, j, (ax << 24) | (rx << 16) | (gx << 8) | (bx));
            }
        }

//            //Gray Scale Image
//            BufferedImageOp op = new ColorConvertOp(  ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//            op.filter(image, colored);
        return tinted;
    }

    protected abstract void flipHoriz(boolean hFlipped);

    protected abstract  void flipVert(boolean vFlipped);
}
