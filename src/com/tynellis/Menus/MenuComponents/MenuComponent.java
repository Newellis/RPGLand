package com.tynellis.Menus.MenuComponents;

import com.tynellis.input.MouseInput;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class MenuComponent {
    protected Rectangle bounds, over;
    protected static int padding = 20;
    protected int offsetX = 0,offsetY = 0;
    protected GuiCompLocations X,Y;

    public void render(Graphics g, int width, int height){
        if (X != null){
            if (X == GuiCompLocations.CENTER) {
                bounds.setLocation((width / 2 - (bounds.width / 2)) + offsetX, bounds.y);
                over.setLocation((width / 2 - (over.width / 2)) + offsetX, over.y);
            } else if (X == GuiCompLocations.START){
                bounds.setLocation(padding + offsetX, bounds.y);
                over.setLocation(padding + offsetX, over.y);
            } else if (X == GuiCompLocations.END){
                bounds.setLocation((width - (bounds.width + padding)) + offsetX, bounds.y);
                over.setLocation((width - (over.width + padding)) + offsetX, over.y);
            }
        }
        if (Y != null){
            if (Y == GuiCompLocations.CENTER) {
                bounds.setLocation(bounds.x, (height / 2 - (bounds.height / 2)) + offsetY);
                over.setLocation(over.x, (height / 2 - (over.height / 2) + 32) + offsetY);
            } else if (Y == GuiCompLocations.START){
                bounds.setLocation(bounds.x, padding + offsetY);
                over.setLocation(over.x, padding + 32 + offsetY);
            } else if (Y == GuiCompLocations.END){
                bounds.setLocation(bounds.x, (height - (bounds.height + padding)) + offsetY);
                over.setLocation(over.x, (height - (over.height + padding) + 32) + offsetY);
            }
        }
    }

    public abstract void tick(MouseInput mouseButtons);
}
