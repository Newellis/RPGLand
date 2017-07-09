package com.tynellis.threadManager;


public class SynchronizingManager {
    public static synchronized void action(Runnable action) {
        action.run();
    }
}
