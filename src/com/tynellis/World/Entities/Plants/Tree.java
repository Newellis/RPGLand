package com.tynellis.World.Entities.Plants;

import com.tynellis.Art.SpriteImage;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Plants.Crops.Crop;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageModifiers.DamageCombinations;
import com.tynellis.World.Entities.damage.DamageModifiers.DamageModifier;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Items.TreeSeeds;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Tree extends Crop {
    private static final SpriteSheet trunk = new SpriteSheet("tempArt/lpc/core/tiles/trunk.png", 96, 96, 1);
    private static final SpriteSheet top = new SpriteSheet("tempArt/lpc/core/tiles/treetop.png", 96, 96, 1);
    private Type treeType;
    private int treeHeight;
    private int maxHeight;

    public enum Type {
        Oak,
        Pine,
    }

    public Tree(Type type, double x, double y, double z, Random rand) {
        this(type, x, y, z, rand, rand.nextInt(100));
    }

    public Tree(Type type, double x, double y, double z, Random rand, int age) {
        super("Tree", x, y, z, rand, 100, age, 20, 20);
        treeType = type;
        maxHeight = 20 + rand.nextInt(20);
        treeHeight = 20 + ((maxHeight - 20) / maxGrowthStage);
        speed = 0.0;
        canBeMoved = false;

        HashMap<Damage.Types, Double> resistances = new HashMap<Damage.Types, Double>();
        resistances.put(Damage.Types.BLUNT, 1.0);
        resistances.put(Damage.Types.FIRE, -1.0);
        resistances.put(Damage.Types.PIERCING, 0.99);
        resistances.put(Damage.Types.FREEZING, 1.0);
        resistances.put(Damage.Types.POISON, 1.0);
        resistance = new DamageModifier(resistances);
        HashMap<Damage.Types, Damage.Types> convert = new HashMap<Damage.Types, Damage.Types>();
        convert.put(Damage.Types.SLICING, Damage.Types.BLUNT);
        damageConverter = new DamageCombinations(convert);

        inventory = new Container(2);
        int amount = ((treeHeight) / (5 + rand.nextInt(6)));
        inventory.addItemPile(new ItemPile(new Log(treeType), amount));
    }

    public Tree(Type type, Random rand) {
        this(type, 0, 0, 0, rand, 0);
    }


    public void performDeath(Region region, Random random) {
        inventory.addItemPile(new ItemPile(new TreeSeeds(treeType, random), random.nextInt(5)));
        super.performDeath(region, random);
    }

    public void tick(Region region, Random random, List<Entity> near) {
        super.tick(region, random, near);
    }

    @Override
    protected void Grow(Random random) {
        treeHeight = 20 + ((maxHeight - 20) / maxGrowthStage);
    }

    @Override
    public Crop newCrop(Random rand, double x, double y, double z) {
        System.out.println("new " + treeType + " tree");
        return new Tree(treeType, x, y, z, rand, 0);
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        int treeNum = 0;
        if (treeType == Type.Oak) treeNum = 0;
        if (treeType == Type.Pine) treeNum = 1;
        BufferedImage trunkFrame = trunk.getSprite(0).getStill(treeNum);
        BufferedImage topFrame = top.getSprite(treeNum).getStill(0);
        if (heal) {
            trunkFrame = SpriteImage.Tint(trunkFrame, Damage.HEAL_COLOR);
            topFrame = SpriteImage.Tint(topFrame, Damage.HEAL_COLOR);
        }
        if (hurt) {
            trunkFrame = SpriteImage.Tint(trunkFrame, Damage.BLEED_COLOR);
            topFrame = SpriteImage.Tint(topFrame, Damage.BLEED_COLOR);
        }
        g.drawImage(trunkFrame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (trunkFrame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - trunkFrame.getHeight() + (2 * height)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        g.drawImage(topFrame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (topFrame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - topFrame.getHeight()) - treeHeight + (height / 2) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        super.render(g, xOffset, yOffset);
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return e.isFlying();
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return movementType == movementTypes.Flying;
    }
}
