package com.tynellis.World.Entities.damage;

import java.awt.*;
import java.io.Serializable;

public class Damage implements Serializable {
    public enum Types {
        //Physical
        BLUNT, SLICING, PIERCING,
        //Environmental
        FIRE, FREEZING, STARVING,
        //Magical
        ARCANE, POISON
    }

    public static final Color BLEED_COLOR = new Color(190, 40, 40);
    public static final Color HEAL_COLOR = new Color(40, 190, 40);

    public Types type;
    public double amount;

    public Damage(Types type, double amount) {
        this.type = type;
        this.amount = amount;
    }
}
