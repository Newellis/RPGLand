package com.tynellis.World.Buildings.Interior;

import com.tynellis.World.Buildings.SmallHouse;
import com.tynellis.World.Entities.UsableEntity.Door;
import com.tynellis.World.Tiles.LandTiles.ManMade.Wall;
import com.tynellis.World.Tiles.LandTiles.ManMade.WoodFloor;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Regions.Generator.WorldGen;
import com.tynellis.World.world_parts.Regions.Region;

import java.io.Serializable;
import java.util.Random;

public class SmallHouseGen extends WorldGen implements Serializable {
    private int x, y, z, height, width;
    private SmallHouse house;
    private Region exitRegion;

    public SmallHouseGen(SmallHouse house, Region exitRegion) {
        this.x = (int) house.getX() + (house.getWidth() / Tile.WIDTH / 2);
        this.y = (int) house.getY() + 1;
        this.z = (int) house.getZ();
        height = house.getHeight();
        width = house.getWidth();
        this.house = house;
        this.exitRegion = exitRegion;
    }

    @Override
    public void fillArea(Region region, int X, int Y, long seed) {
        Random rand = new Random(seed * ((X * Region.WIDTH) + Y)); // for location based randoms

        for (int y = Y; y < Y + Area.HEIGHT; y++) {
            for (int x = X; x < X + Area.WIDTH; x++) {
                if (y <= this.y && y > this.y - height / Tile.HEIGHT && x <= this.x && x > this.x - width / Tile.WIDTH) {
                    region.setTile(new WoodFloor(rand, 100), x, y, z); // add land
                } else {
                    region.setTile(new Wall(rand, 100, house.getWallType()), x, y, z); // add land
                }
            }
        }
    }

    @Override
    public void styleArea(Region region, int x, int y, long seed) {

    }

    @Override
    public void populateArea(Region region, int x, int y, long seed) {
        region.addEntity(new Door(house.getDoor().getX(), house.getDoor().getY() + 1, z, 1, exitRegion));
    }
}
