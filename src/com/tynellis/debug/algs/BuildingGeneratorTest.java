package com.tynellis.debug.algs;

import com.tynellis.World.Buildings.Building;
import com.tynellis.World.Buildings.SmallHouse;
import com.tynellis.World.Tiles.Tile;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class BuildingGeneratorTest extends AlgTest implements ActionListener {

    JPanel infoPanel;
    Building building;
    JTextField width, height, floors;

    public BuildingGeneratorTest() {
        Name = "Building Gen";

    }

    @Override
    public boolean Start() {
        setLayout(new BorderLayout());
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        width = new JTextField("width");
        height = new JTextField("height");
        floors = new JTextField("floors");
        JButton test = new JButton("Generate");
        test.addActionListener(this);
        infoPanel.add(width);
        infoPanel.add(height);
        infoPanel.add(floors);
        infoPanel.add(test);
        add(infoPanel, BorderLayout.LINE_END);
        return false;
    }

    @Override
    public boolean Step() {
        return false;
    }

    @Override
    protected void render(Graphics g) {
        if (building != null) {
            building.render(g, 0, 0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        building = new SmallHouse(((getWidth() - infoPanel.getWidth()) / Tile.WIDTH) / 2.0, ((getHeight() / Tile.HEIGHT) + Integer.parseInt(height.getText())) / 2, 0, Integer.parseInt(width.getText()), Integer.parseInt(height.getText()), new Random());
        repaint();
    }
}
