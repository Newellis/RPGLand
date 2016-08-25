package com.tynellis.Art;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Sprite extends SpriteImage {
    private BufferedImage[] sprite;
    private boolean hFlipped = false;
    private boolean vFlipped = false;

    public Sprite(BufferedImage[] sprite) {
        this.sprite = sprite;
    }

    public Sprite(String path, int width, int scale) {
        sprite = slice(path, width, scale);
    }

    public BufferedImage getStill(int num) {
        return sprite[num];
    }

    public void flipHoriz(boolean hFlipped) {
        if (this.hFlipped != hFlipped) {
            for (int i = 0; i < sprite.length; i++) {
                AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
                tx.translate(-sprite[i].getWidth(null), 0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                sprite[i] = op.filter(sprite[i], null);
            }
        }
        this.hFlipped = hFlipped;
    }

    public void flipVert(boolean vFlipped) {
        if (this.vFlipped != vFlipped) {
            for (int i = 0; i < sprite.length; i++) {
                AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
                tx.translate(0, -sprite[i].getHeight());
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                sprite[i] = op.filter(sprite[i], null);
            }
        }
        this.vFlipped = vFlipped;
    }

    public BufferedImage[] getSprite(){
        return sprite;
    }
}
