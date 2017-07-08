package com.tynellis.Menus;

import com.tynellis.GameComponent;
import com.tynellis.Menus.MenuComponents.Button;
import com.tynellis.Menus.MenuComponents.ButtonListener;
import com.tynellis.Menus.MenuComponents.MenuComponent;
import com.tynellis.input.MouseInput;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public abstract class Menu extends MenuComponent implements ButtonListener{

    protected String Flavor = "";
    protected List<Button> buttons = new ArrayList<Button>();

    protected Button addButton(Button button) {
        buttons.add(button);
        button.addListener(this);
        return button;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        for (Button button : buttons) {
            button.render(g, width, height);
        }
    }

    public void tick(GameComponent game, MouseInput mouse, int width, int height) {
        tick(mouse);
    }

    public void setFlavor(String update) {
        Flavor = update;
    }

    @Override
    public void tick(MouseInput mouseButtons) {
        for (Button button : buttons) {
            button.tick(mouseButtons);
        }
    }

    public void addButtonListener(ButtonListener listener) {
        for (Button button : buttons) {
            button.addListener(listener);
        }
    }
}
