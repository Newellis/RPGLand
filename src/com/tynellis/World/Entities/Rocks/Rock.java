package com.tynellis.World.Entities.Rocks;

import com.tynellis.Art.SpriteImage;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageModifiers.DamageCombinations;
import com.tynellis.World.Entities.damage.DamageModifiers.DamageModifier;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Stone;
import com.tynellis.World.Tiles.Tile;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Random;

public class Rock extends KillableEntity {

    private static final SpriteSheet Rock = new SpriteSheet("tempArt/lpc/core/tiles/rock.png", 32, 32, 1);

    public Rock(double x, double y, double z, Random rand) {
        super(x, y, z, 32, 32);
        speed = 0.0;
        canBeMoved = false;

        HashMap<Damage.Types, Double> resistances = new HashMap<Damage.Types, Double>();
        resistances.put(Damage.Types.PIERCING, 0.9);
        resistances.put(Damage.Types.FIRE, 1.0);
        resistances.put(Damage.Types.FREEZING, 1.0);
        resistances.put(Damage.Types.SLICING, 1.0);
        resistances.put(Damage.Types.POISON, 1.0);
        resistances.put(Damage.Types.BLUNT, -0.5);
        resistance = new DamageModifier(resistances);
        HashMap<Damage.Types, Damage.Types> convert = new HashMap<Damage.Types, Damage.Types>();
        convert.put(Damage.Types.BLUNT, Damage.Types.PIERCING);
        damageConverter = new DamageCombinations(convert);

        inventory = new Container(5);
        inventory.addItemPile(new ItemPile(new Stone(), 1 + rand.nextInt(4)));
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        BufferedImage rock = Rock.getSprite(0).getStill(0);
        if (hurt) {
            rock = SpriteImage.Tint(rock, Damage.BLEED_COLOR);
        }
        g.drawImage(rock, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (rock.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - rock.getHeight() + (0.5 * height)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
        super.render(g, xOffset, yOffset);
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return e.isFlying();
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return movementType == movementTypes.Flying;
    }

    @Override
    public int compareTo(Entity entity) {
        return 0;
    }
}
