package com.tynellis.Menus;

import com.tynellis.Menus.MenuComponents.Button;

import java.awt.Color;
import java.awt.Graphics;

public class LoadScreen extends Menu {
    private String Flavor = "Saving";

    public void render(Graphics g, int width, int height) {
        g.setColor(Color.WHITE);
        g.drawString("Loading", 100, 200);
        g.drawString(Flavor, 100, 240);
    }

    public void setFlavor(String update){
        Flavor = update;
    }

    @Override
    public void buttonPressed(Button button) {

    }
}
