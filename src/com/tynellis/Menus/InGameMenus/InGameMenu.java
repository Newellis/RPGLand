package com.tynellis.Menus.InGameMenus;

import com.tynellis.GameComponent;
import com.tynellis.Menus.Menu;
import com.tynellis.World.World;
import com.tynellis.input.MouseInput;

public abstract class InGameMenu extends Menu {
    protected World world;

    public void tick(GameComponent game, MouseInput mouse, int width, int height, World world) {
        this.world = world;
        tick(mouse);
    }

    public abstract void closeMenu();
}
