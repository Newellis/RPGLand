package com.tynellis.Save;

import com.tynellis.World.World;

import java.io.Serializable;

public class SavedWorld implements Serializable {
    private World world;
    private String playerName;

    public SavedWorld(World world, String playerName) {
        this.world = world;
        this.playerName = playerName;
    }

    public World getWorld() {
        return world;
    }

    public String getPlayerName() {
        return playerName;
    }
}
