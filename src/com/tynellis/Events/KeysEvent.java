package com.tynellis.Events;

import com.tynellis.GameComponent;
import com.tynellis.GameState;
import com.tynellis.Menus.PauseMenu;
import com.tynellis.input.Keys;

/**
 * Created by tyler on 7/26/15.
 */
public class KeysEvent extends TickEvent {
    private Keys keys;

    public KeysEvent(Keys keys){
        this.keys = keys;
    }

    @Override
    public void run(EventHandler handler) {
        keys.tick();
        super.run(handler);
    }
}
