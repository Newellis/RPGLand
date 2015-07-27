package com.tynellis.Art;

import com.tynellis.GameComponent;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

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
    protected abstract void flipHoriz(boolean hFlipped);

    protected abstract  void flipVert(boolean vFlipped);
}
