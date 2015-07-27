package com.tynellis.Menus;

import com.tynellis.GameComponent;
import com.tynellis.Menus.MenuComponents.Button;
import com.tynellis.Menus.MenuComponents.GuiCompLocations;
import com.tynellis.input.MouseInput;

import java.awt.Graphics;

public class MainMenu extends Menu {
    private boolean SinglePlayerClicked = false, QuitClicked = false;
    private String singlePlayer = "SinglePlayer", quit = "Quit";

    public MainMenu(int gameWidth, int gameHeight) {
        super();
        addButton(new Button(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, -(gameHeight / 12), singlePlayer, true));
        addButton(new Button(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, 0, quit, true));
        //charPos = ((gameHeight / 12) * 5) - 5;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        super.render(g, width, height);
    }

    public void tick(GameComponent game, MouseInput mouse, int width, int height){
        if (SinglePlayerClicked) {
            game.setMenu(new SinglePlayerGameMenu(width, height));
            SinglePlayerClicked = false;
        }else if(QuitClicked){
            game.Quit();
            QuitClicked = false;
        }
        super.tick(game, mouse, width, height);
    }

    @Override
    public void buttonPressed(Button button) {
        if(button.getName().equals(singlePlayer)){
            SinglePlayerClicked = true;
        }else if(button.getName().equals(quit)){
            QuitClicked = true;
        }
    }
}
