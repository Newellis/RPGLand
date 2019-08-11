package com.tynellis.World.Entities.Living.animals;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Entities.damage.DamageSource;
import com.tynellis.World.Items.Food.Food;
import com.tynellis.World.world_parts.Regions.Region;

import java.util.List;
import java.util.Random;

public abstract class Animal extends LivingEntity {

    public enum Diet {
        HERBIVORE,
        CARNIVORE,
        OMNIVORE,
        ;
    }

    private double nutrition;
    private final double maxNutrition;
    private double starvationNum;
    private boolean starving = false;
    private double foodUsage;

    protected Animal(String name, int x, int y, int z, Random random) {
        super(name, x, y, z, Gender.randGender(random));
        System.out.println("New " + name + " that's a " + getGender());
        maxNutrition = 20;
        starvationNum = 5;
        nutrition = random.nextInt((int) (maxNutrition - starvationNum - 1)) + random.nextDouble() + starvationNum;
        foodUsage = 0.0001;
    }

    public void tick(Region region, Random random, List<Entity> near) {
        if (nutrition > maxNutrition / 2.0) {
            Heal(1);
        }
        consumeNutrition(random);
        super.tick(region, random, near);
    }

    private void consumeNutrition(Random rand) {
        if (nutrition < starvationNum && nutrition > 0) {
            starving = true;
            if (rand.nextDouble() < 0.01) {
                DamageBy(new DamageSource(new Damage(Damage.Types.STARVING, 1)), rand);
            }
            nutrition -= foodUsage / 2.0;
        } else {
            nutrition -= foodUsage;
        }
        if (nutrition < 0) {
            DamageBy(new DamageSource(new Damage(Damage.Types.STARVING, 2)), rand);
            nutrition = 0;
        }
    }

    public boolean canEat() {
        return nutrition < maxNutrition;
    }

    public boolean eatFood(Food food) {
        if (canEat()) {
            nutrition += food.getNutrition();
            if (nutrition > maxNutrition) {
                Heal((int) Math.ceil(nutrition - maxNutrition));
                nutrition = maxNutrition;
            }
            return true;
        }
        return false;
    }

    public boolean isStarving() {
        return starving;
    }

    public boolean wantsToEat(Random random) {
        if (starving) {
            return true;
        } else {
            double randNum = random.nextDouble();
            boolean hungry = (1.0 - (nutrition / maxNutrition)) > randNum && random.nextBoolean();
            return hungry;
        }
    }
}
