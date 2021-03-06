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

    protected List<Button> buttons = new ArrayList<Button>();
    private List<MenuComponent> components = new ArrayList<MenuComponent>();

    protected Button addButton(Button button) {
        buttons.add(button);
        button.addListener(this);
        return button;
    }

    protected MenuComponent addComponent(MenuComponent component) {
        components.add(component);
        return component;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        for (Button button : buttons) {
            button.render(g, width, height);
        }
        for (MenuComponent component : components) {
            component.render(g, width, height);
        }
    }

    public void tick(GameComponent game, MouseInput mouse, int width, int height) {
        tick(mouse);
    }

    @Override
    public void tick(MouseInput mouseButtons) {
        for (Button button : buttons) {
            button.tick(mouseButtons);
        }
        for (MenuComponent component : components) {
            component.tick(mouseButtons);
        }
    }

    public void addButtonListener(ButtonListener listener) {
        for (Button button : buttons) {
            button.addListener(listener);
        }
    }
}
