package com.tynellis.Menus;

import com.tynellis.GameComponent;
import com.tynellis.GameState;
import com.tynellis.Menus.MenuComponents.Button;
import com.tynellis.Menus.MenuComponents.GuiCompLocations;
import com.tynellis.Menus.MenuComponents.SaveGameButton;
import com.tynellis.input.MouseInput;

import java.awt.Graphics;

public class SinglePlayerGameMenu extends Menu{
    private boolean BackClicked = false;
    private String back = "Back";
    private String W1 = "World1", W2 = "World2", W3 = "World3", W4 = "World4", W5 = "World5";
    private SaveGameButton Save1, Save2, Save3, Save4, Save5;
    private boolean World1 = false, World2 = false, World3 = false, World4 = false,  World5 = false;

    public SinglePlayerGameMenu(int gameWidth, int gameHeight) {
        super();
        Save1 = new SaveGameButton(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, -((gameHeight / 12) * 2), W1);
        Save2 = new SaveGameButton(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, -((gameHeight / 12)), W2);
        Save3 = new SaveGameButton(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, 0, W3);
        Save4 = new SaveGameButton(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, ((gameHeight / 12)), W4);
        Save5 = new SaveGameButton(GuiCompLocations.CENTER, 0, GuiCompLocations.CENTER, ((gameHeight / 12) * 2), W5);
        addButton(Save1);
        addButton(Save2);
        addButton(Save3);
        addButton(Save4);
        addButton(Save5);
        addButton(new Button(GuiCompLocations.END, 0, GuiCompLocations.END, 0, back, true));
    }

    public void render(Graphics g, int width, int height) {
        super.render(g, width, height);
    }

    public void tick(GameComponent game, MouseInput mouse, int width, int height){
        if(World1) {
            Save1.Click(game);
        }else if(World2) {
            Save2.Click(game);
        }else if(World3) {
            Save3.Click(game);
        }else if(World4) {
            Save4.Click(game);
        }else if(World5) {
            Save5.Click(game);
        }else if(BackClicked){
            game.setState(GameState.MENU);
            game.setMenu(new MainMenu(width, height));
        }
        super.tick(game, mouse, width, height);
    }

    public void buttonPressed(Button button) {
        if(button instanceof SaveGameButton){
            if(((SaveGameButton) button).getGame().equals(W1)){
                World1 = true;
            } else if(((SaveGameButton) button).getGame().equals(W2)){
                World2 = true;
            } else if(((SaveGameButton) button).getGame().equals(W3)){
                World3 = true;
            }else if(((SaveGameButton) button).getGame().equals(W4)){
                World4 = true;
            }else if(((SaveGameButton) button).getGame().equals(W5)){
                World5 = true;
            }
        }else if(button.getName().equals(back)){
            BackClicked = true;
        }
    }
}
