package com.tynellis.Art;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Still extends SpriteImage {
    private BufferedImage still;
    private boolean hFlipped = false;
    private boolean vFlipped = false;

    public Still(String path, int scale) {
        still = load(path, scale);
    }

    public Still(BufferedImage still) {
        this.still = still;
    }

    public BufferedImage getImage() {
        return still;
    }

    public void flipHoriz(boolean hFlipped) {
        if (this.hFlipped != hFlipped) {
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-still.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            still = op.filter(still, null);
        }
        this.hFlipped = hFlipped;
    }

    public void flipVert(boolean vFlipped) {
        if (this.vFlipped != vFlipped) {
            AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -still.getHeight());
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            still = op.filter(still, null);
        }
        this.vFlipped = vFlipped;
    }
}
