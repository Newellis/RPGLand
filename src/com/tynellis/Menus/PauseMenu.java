package com.tynellis.Menus;

import com.tynellis.GameComponent;
import com.tynellis.GameState;
import com.tynellis.Menus.MenuComponents.Button;
import com.tynellis.Menus.MenuComponents.GuiCompLocations;
import com.tynellis.input.MouseInput;

import java.awt.Graphics;

public class PauseMenu extends Menu {
    private boolean ResumeClicked = false, SaveClicked, ExitClicked = false;
    private String resume = "Resume", save = "Save", quit = "Quit";

    public PauseMenu(int gameWidth, int gameHeight) {
        super();
        addButton(new Button(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, -(gameHeight / 12), resume, true));
        addButton(new Button(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, 0, save, true));
        addButton(new Button(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, (gameHeight / 12), quit, true));
    }

    public void render(Graphics g, int width, int height) {
        super.render(g, width, height);
    }

    public void tick(GameComponent game, MouseInput mouse, int width, int height) {
        super.tick(game,mouse, width, height);
        if(ResumeClicked){
            game.setState(GameState.SINGLE_PLAYER);
        } else if (SaveClicked) {
            game.saveWorld();
            SaveClicked = false;
        }else if (ExitClicked) {
            game.saveWorld();
            game.setState(GameState.MENU);
            game.setMenu(new MainMenu(width, height));
        }
    }

    @Override
    public void buttonPressed(Button button) {
        if(button.getName().equals(resume)){
            ResumeClicked = true;
        } else if (button.getName().equals(save)) {
            SaveClicked = true;
        } else if(button.getName().equals(quit)){
            ExitClicked = true;
        }
    }
}
