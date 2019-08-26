package com.tynellis.World.world_parts.Regions.Generator;

import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Regions.Region;

import java.io.Serializable;
import java.util.Random;

public abstract class WorldGen implements Serializable {
    public abstract void fillArea(Region region, int x, int y, long seed);

    public abstract void styleArea(Region region, int x, int y, long seed);

    public abstract void populateArea(Region region, int x, int y, long seed);

    protected void erode(Region region, int X, int Y, Random rand) {
        for (int i = 0; i < 2; i++) { //number of times to erode
            for (int x = X; x < X + Area.WIDTH; x++) {
                for (int y = Y; y < Y + Area.HEIGHT; y++) {
                    Tile[][] adjacent = region.getAdjacentTiles(x, y, 0);
                    Tile tile = region.getTile(x, y, 0), topTile = null;
                    if (tile == null) {
                        continue;
                    }
                    int rank = tile.getRankNum(), count = 0;
                    if (adjacent[2][1] != null && adjacent[2][1].getRankNum() >= rank) {
                        count++;
                    } else if (adjacent[2][1] != null) {
                        topTile = adjacent[2][1];
                    }
                    if (adjacent[0][1] != null && adjacent[0][1].getRankNum() >= rank) {
                        count++;
                    } else if (topTile == null || adjacent[0][1] != null && adjacent[0][1].getRankNum() < topTile.getRankNum()) {
                        topTile = adjacent[0][1];
                    }
                    if (adjacent[1][2] != null && adjacent[1][2].getRankNum() >= rank) {
                        count++;
                    } else if (topTile == null || adjacent[1][2] != null && adjacent[1][2].getRankNum() < topTile.getRankNum()) {
                        topTile = adjacent[1][2];
                    }
                    if (adjacent[1][0] != null && adjacent[1][0].getRankNum() >= rank) {
                        count++;
                    } else if (topTile == null || adjacent[1][0] != null && adjacent[1][0].getRankNum() < topTile.getRankNum()) {
                        topTile = adjacent[1][0];
                    }
                    if (count < 2 && topTile != null) {
                        Tile newTile = topTile.newTile(rand, tile.getHeightInWorld());
                        region.setTile(newTile, x, y, 0);

                    }
                }
            }
        }
    }
}
