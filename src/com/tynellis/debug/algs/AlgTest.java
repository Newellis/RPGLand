package com.tynellis.debug.algs;

import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Graphics;

public abstract class AlgTest extends JPanel {
    public String Name;

    public abstract boolean Start();

    public abstract boolean Step();

    public Container draw() {
        repaint();
        return this;
    }

    protected abstract void render(Graphics g);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }
}
