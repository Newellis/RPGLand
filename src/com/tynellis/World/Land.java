package com.tynellis.World;

import com.tynellis.World.Tiles.Tile;

public interface Land{
    Tile getTile(int X, int Y, int Z);
    void setTile(Tile tile, int X, int Y, int Z);
}
