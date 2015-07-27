package com.tynellis.Art;

import java.awt.image.BufferedImage;

public class Animation {
    private SpriteSheet sheet;
    private final int delay;
    private int lastFrame = 0;
    private long lastFrameTime = 0;
    private int startFrame = 0;
    private int endFrame = 0;
    private int frameRow = 0;
    private boolean paused = false;

    public Animation(SpriteSheet sheet, int frameDelayTicks) {
        delay = frameDelayTicks;
        this.sheet = sheet;
    }
    public Animation(int frameDelayTicks){
        delay = frameDelayTicks;
    }


    public void tick() {
        if (!paused) {
            lastFrameTime += 1;
        }
    }

    public BufferedImage getFrame() {
        if (lastFrameTime >= delay && !paused) {
            lastFrame += 1;
            if (lastFrame >= endFrame) {
                lastFrame -= endFrame - startFrame;
            }
            lastFrameTime = 0;
        }
        return sheet.getSprite(frameRow).getSprite()[lastFrame];
    }

    public void pause() {
        paused = true;
    }

    public void play() {
        paused = false;
    }

    public void playInRange(int row, int start, int end){
        paused = false;
        lastFrame = start;
        lastFrameTime = 0;
        startFrame = start;
        endFrame = end;
        frameRow = row;
    }

    public void playFromStart(int row) {
        paused = false;
        lastFrame = 0;
        frameRow = row;
        lastFrameTime = 0;
        startFrame = 0;
        endFrame = sheet.getSprite(row).getSprite().length;

    }

    public void nextFrame() {
        lastFrame += 1;
        lastFrameTime = 0;
    }

    public int getFrameNum() {
        return lastFrame;
    }

    public void skipToFrame(int frame) {
        lastFrame = frame;
        lastFrameTime = 0;
    }

    public void setRow(int row) {
        frameRow = row;
    }
}
