package com.tynellis.input;

import java.util.ArrayList;
import java.util.List;

public class Keys {
    public final class Key {
        private boolean nextState = false;
        public boolean wasDown = false;
        public boolean isDown = false;

        public Key() {
            all.add(this);
        }

        public void tick() {
            wasDown = isDown;
            isDown = nextState;
        }

        public boolean wasPressed() {
            return !wasDown && isDown;
        }

        public boolean wasReleased() {
            return wasDown && !isDown;
        }

        public void release() {
            nextState = false;
        }

        public void setNextState(boolean nextState) {
            this.nextState = nextState;
        }
    }

    private List<Key> all = new ArrayList<Key>();

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key debug = new Key();
    public Key pause = new Key();

    public void tick() {
        for (Key key : all)
            key.tick();
    }

    public void release() {
        for (Key key : all)
            key.release();
    }

    public List<Key> getAll() {
        return all;
    }
}
