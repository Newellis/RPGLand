package com.tynellis.Save;

import com.tynellis.World.world_parts.Region;

import java.io.Serializable;

public class SavedWorld implements Serializable {
    private Region region;
    private String playerName;

    public SavedWorld(Region region, String playerName) {
        this.region = region;
        this.playerName = playerName;
    }

    public Region getRegion() {
        return region;
    }

    public String getPlayerName() {
        return playerName;
    }
}
