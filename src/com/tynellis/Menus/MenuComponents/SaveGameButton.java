package com.tynellis.Menus.MenuComponents;

import com.tynellis.GameComponent;
import com.tynellis.GameState;
import com.tynellis.Save.FileHandler;
import com.tynellis.Save.InvalidSaveException;
import com.tynellis.input.MouseInput;

import java.util.Random;

public class SaveGameButton extends Button {
    private String game;
    private boolean valid = false;
    public SaveGameButton(GuiCompLocations x, int xOffset, GuiCompLocations y, int yOffset, String game) {
        super(x, xOffset, y, yOffset, "New Game", true);
        this.game = game;
        updateButton();
    }
    @Override
    public void tick(MouseInput mouseButtons) {
        updateButton();
        super.tick(mouseButtons);
    }
    public String getGame() {
        return game;
    }

    public void Click(GameComponent game){
        if (valid){
            try {
                game.loadWorld(this.game);
                FileHandler.setGameDir(this.game);
                game.setState(GameState.SINGLE_PLAYER);
            } catch (InvalidSaveException e) {
                e.printStackTrace();
            }
        }else {
            long seed = 2000000;//for testing purposes should be new Random().nextLong();
            game.startGame(this.game, seed);
        }
    }

    private void updateButton(){
        if (FileHandler.checkGameDir(game)) {
            Word = "Load " + game;
            valid = true;
        } else {
            Word = "New Game";
            valid = false;
        }
    }
}
