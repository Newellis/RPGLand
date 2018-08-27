package com.tynellis.World.Entities.damage;

public class Damage {
    public enum Types {
        //Physical
        BLUNT, SLICING, PIERCING,
        //Environmental
        FIRE, FREEZING,
        //Magical
        ARCANE,
    }

    public Types type;
    public double amount;

    public Damage(Types type, double amount) {
        this.type = type;
        this.amount = amount;
    }
}
