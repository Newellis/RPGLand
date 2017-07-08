package com.tynellis.Menus;

import com.tynellis.Menus.MenuComponents.Button;

import java.awt.Color;
import java.awt.Graphics;

public class LoadScreen extends Menu {
    private int percent = 0;

    public LoadScreen() {
        Flavor = "Generating World";
    }

    public void render(Graphics g, int width, int height) {
        g.setColor(Color.WHITE);
        g.drawString("Loading", 100, 200);
        g.drawString(Flavor, 100, 240);
    }

    @Override
    public void buttonPressed(Button button) {

    }
}
