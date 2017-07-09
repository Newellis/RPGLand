package com.tynellis.threadManager;

import com.tynellis.GameRenderer;

import java.awt.Graphics;

public class RenderAction implements Runnable {
    private GameRenderer game;
    private Graphics g;

    public RenderAction(GameRenderer game, Graphics g) {
        this.game = game;
        this.g = g;
    }

    @Override
    public void run() {
        game.render(g);
    }
}
