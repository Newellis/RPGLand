package com.tynellis.World.Tiles.LandTiles.Natural;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.animals.Animal;
import com.tynellis.World.Items.Food.Food;
import com.tynellis.World.Tiles.LandTiles.LandTile;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.Random;

public class Grass extends Dirt implements Food {
    private static final SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/core/tiles/terain/grass.png", 32, 32, 1);
    private static final SpriteSheet ALT = new SpriteSheet("tempArt/lpc/core/tiles/terain/grassalt.png", 32, 32, 1);
    //waprivate static final SpriteSheet WATER = new SpriteSheet("tempArt/lpc/mine/grasswater.png", 32, 32, 1);
    private static final double altPercent = 0.10;

    public Grass(Random rand, int height) {
        super("Grass", SHEET, rand, altPercent, TileRank.Grass, height);
    }

    public SpriteSheet getSheet(Tile tile) {
        if (tile != null && tile.getName().compareTo("Water") == 0) {
            return ALT;
        }
        return top;
    }

    @Override
    protected void setSprite() {
        top = SHEET;
    }

    @Override
    public void update(Region region, Tile[][] adjacent, int x, int y, int z, Random rand) {
    }

    @Override
    public LandTile newTile(Random rand, int height) {
        return new Grass(rand, height);
    }

    @Override
    public FoodType getType() {
        return FoodType.PLANT;
    }

    @Override
    public boolean canEat(Animal.Diet e) {
        return e == Animal.Diet.HERBIVORE;
    }

    @Override
    public double getNutrition() {
        return 0.5;
    }

    @Override
    public void doneEating(Entity e) {

    }
}
