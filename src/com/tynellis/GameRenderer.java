package com.tynellis;


import com.tynellis.Menus.Menu;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GameRenderer extends JPanel implements Runnable {
    public static int maxFPS = 60;
    public static boolean running;
    private int fps;
    private transient GameComponent game;
    private World world;
    private transient BufferedImage screenFrame;
    private double X, Y, Z;


    public GameRenderer(GameComponent component) {
        this.setSize(new Dimension(component.getWidth(), component.getHeight()));
        game = component;
    }

    @Override
    public void run() {
        running = true;
        int frames = 0;
        long lastTimer1 = System.currentTimeMillis();

        long lastRenderTime = System.nanoTime();
        long lastRenderTimeStart = System.nanoTime() - 1000000000;
        int min = 999999999;
        int max = 0;
        System.out.println("render Start");
        while (running) {
            long nanoSecsBetweenRender = 1000000000 / maxFPS;
            long secsBetweenRender = System.nanoTime() - lastRenderTimeStart;

            if (secsBetweenRender >= nanoSecsBetweenRender) {
                frames++;
                Graphics g = getGraphics();
                lastRenderTimeStart = System.nanoTime();

                render(g);

                long renderTime = System.nanoTime();
                int timePassed = (int) (renderTime - lastRenderTime);
                if (timePassed < min) {
                    min = timePassed;
                }
                if (timePassed > max) {
                    max = timePassed;
                }
                lastRenderTime = renderTime;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                fps = frames;
                frames = 0;
            }
        }
        System.exit(0);
    }


    private synchronized void render(Graphics g) {
        int height = game.getHeight();
        int width = game.getWidth();

        if (screenFrame == null || screenFrame.getWidth() != width || screenFrame.getHeight() != height) {
            screenFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics screen = screenFrame.getGraphics();
        screen.setColor(Color.BLACK);
        screen.fillRect(0, 0, width, height);
        GameState state = game.getState();
        Menu menu = game.getMenu();

        //fill screen here
        if (state == GameState.SINGLE_PLAYER || state == GameState.IN_GAME_MENU || state == GameState.PAUSE_MENU) {
            System.out.println("render World");
            world.render(screen, width, height, (int) ((X + 0.5) * Tile.WIDTH), (int) ((Y + 0.5) * Tile.HEIGHT), (int) (Z * (Tile.HEIGHT * 3 / 4)));
        } else if (state == GameState.MENU) {
            menu.render(screen, width, height);
        }
        if (state == GameState.IN_GAME_MENU) {
            menu.render(screen, width, height);
        } else if (state == GameState.PAUSE_MENU) {
            screen.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
            screen.fillRect(0, 0, width, height);
            menu.render(screen, width, height);
        }
        screen.setColor(Color.WHITE);
        screen.drawString("FPS: " + fps, 10, 20);
        g.drawImage(screenFrame, 0, 0, null);
    }

    public void setWorld(World world) {
        System.out.println(world);
        this.world = world;
    }

    public void setXYZ(double x, double y, double z) {
        X = x;
        Y = y;
        Z = z;
    }
}
