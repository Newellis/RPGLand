package com.tynellis.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

public class KeyInput implements KeyListener {
    private Map<Integer, Keys.Key> mappings = new HashMap<Integer, Keys.Key>();

    public KeyInput(Keys keys) {
        mappings.put(KeyEvent.VK_W, keys.up);
        mappings.put(KeyEvent.VK_S, keys.down);
        mappings.put(KeyEvent.VK_A, keys.left);
        mappings.put(KeyEvent.VK_D, keys.right);
        mappings.put(KeyEvent.VK_T, keys.debug);
        mappings.put(KeyEvent.VK_ESCAPE, keys.pause);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        toggle(keyEvent, true);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        toggle(keyEvent, false);
    }

    private void toggle(KeyEvent ke, boolean state) {
        Keys.Key key = mappings.get(ke.getKeyCode());
        if (key != null) {
            key.setNextState(state);
        }
    }
}
