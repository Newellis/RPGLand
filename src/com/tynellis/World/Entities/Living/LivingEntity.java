package com.tynellis.World.Entities.Living;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteImage;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.GameComponent;
import com.tynellis.World.Entities.AttackingEntity;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.Ai.NpcAi;
import com.tynellis.World.Entities.Living.Ai.Pathfinding.Core.PathfinderAi;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Items.Tools.Weapons.Sword;
import com.tynellis.World.Items.Tools.Weapons.Weapon;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;
import com.tynellis.debug.Debug;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class LivingEntity extends AttackingEntity {
    public enum Gender {
        MALE,
        FEMALE,
        OTHER,
        BOTH,
        ;

        public static Gender randGender(Random random) {
            return randGender(random, true);
        }

        public static Gender randGender(Random rand, boolean binary) {
            if (!binary) {
                int choice = rand.nextInt(Gender.values().length);
                return Gender.values()[choice];
            } else {
                return (rand.nextBoolean()) ? MALE : FEMALE;
            }
        }
    }
    private String name;
    protected transient SpriteSheet spriteSheet;
    protected transient Animation animation;
    protected transient SpriteSheet attackSheet;
    protected transient Animation attackAnimation;
    private Gender gender;
    protected NpcAi Ai = new NpcAi();
    protected PathfinderAi pathfinder = new PathfinderAi();


    protected Weapon weapon = new Sword("Murder Town", 25, 5, 2);


    protected LivingEntity(String name, int x, int y, int z, Gender gender) {
        super(x, y, z, 32, 32);
        this.name = name;
        this.gender = gender;
        setSprite(gender);
    }

    public LivingEntity(int x, int y, int z, Gender gender, Random random) {
        this(getName(gender, random), x, y, z, gender);
        setLooking(random.nextInt(4));
    }

    public LivingEntity(int x, int y, int z, Random random) {
        this(x, y, z, Gender.values()[random.nextInt(Gender.values().length)], random);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setSprite(gender);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    protected void setSprite(Gender gender) {
        if (gender == Gender.MALE) {
            spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
            attackSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_slash.png", 64, 64, 1);
        } else if (gender == Gender.FEMALE) {
            spriteSheet = new SpriteSheet("tempArt/lpc/core/char/female/female_walkcycle.png", 64, 64, 1);
            attackSheet = new SpriteSheet("tempArt/lpc/core/char/female/female_slash.png", 64, 64, 1);
        } else {
            spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
            attackSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_slash.png", 64, 64, 1);
        }
        animation = new Animation(spriteSheet, 5);
        animation.playInRange(spriteFacing, 1, 8);
        attackAnimation = new Animation(attackSheet, 2);
        attackAnimation.playFromStart(spriteFacing);
    }

    public static String getName(Gender gender, Random random) {
        InputStream names;
        if (gender == Gender.MALE) {
            names = GameComponent.class.getResourceAsStream("names/MaleNames.txt");
        } else if (gender == Gender.FEMALE) {
            names = GameComponent.class.getResourceAsStream("names/FemaleNames.txt");
        } else if (gender == Gender.OTHER) {
            names = GameComponent.class.getResourceAsStream("names/GenderlessNames.txt");
        } else {
            if (random.nextInt(2) == 0) {
                names = GameComponent.class.getResourceAsStream("names/MaleNames.txt");
            } else {
                names = GameComponent.class.getResourceAsStream("names/FemaleNames.txt");
            }
        }
        String result = "Newell";
        int n = 0;
        for (Scanner sc = new Scanner(names); sc.hasNext(); ) {
            ++n;
            String line = sc.nextLine();
            if (random.nextInt(n) == 0)
                result = line;
        }
        return result;
    }

    public void tick(Region region, Random random, List<Entity> near) {
        Ai.tick(region, random, this);
//        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.PATH)) {
//            moving = false;
//        }
        super.tick(region, random, near);
        animation.setRow(spriteFacing);
        attackAnimation.setRow(spriteFacing);
        weapon.coolDownTick();
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        BufferedImage frame;
        if (!moving) {
            animation.pause();
            animation.skipToFrame(0);
        } else {
            animation.play();
        }
        if (!attacking) {
            frame = animation.getFrame();
            if (hurt) {
                frame = SpriteImage.Tint(frame, Damage.BLEED_COLOR);
            }
            g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (frame.getHeight() - height / 2.0)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
            animation.tick();
        }

        if (!attacking) {
            attackAnimation.pause();
            attackAnimation.skipToFrame(0);
        } else {
            attackAnimation.play();
            if (attackAnimation.getFrameNum() == 5) {
                attacking = false;
            }
            frame = attackAnimation.getFrame();
            if (hurt) {
                frame = SpriteImage.Tint(frame, Damage.BLEED_COLOR);
            }
            g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (frame.getHeight() - height / 2.0)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
            attackAnimation.tick();
            weapon.renderAttack(g, xOffset, yOffset, this);
        }
        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.PATH)) {
            List<Node> nodes = pathfinder.getPath();
            if (nodes != null) {
                for (Node node : nodes) {
                    node.render(g, xOffset, yOffset);
                }
            }
        }
        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.ATTACK)) {
            Rectangle rectangle = weapon.getAttackArea(this);
            g.setColor(Color.RED);
            g.drawRect(rectangle.x + xOffset, rectangle.y + yOffset, rectangle.width, rectangle.height);
            Point2D point = weapon.getAttackPoint(this);
            if (point != null) {
                g.setColor(Color.ORANGE);
                g.drawOval(xOffset + (int) (point.getX() * Tile.WIDTH), yOffset + (int) (point.getY() * Tile.HEIGHT), 3, 3);
            }
        }
        super.render(g, xOffset, yOffset);
    }

    public PathfinderAi getPathfinder() {
        return pathfinder;
    }

    public boolean canHit(Region region, Entity target) {
        if (weapon.canUse(region, this)) {

            Rectangle area = weapon.getAttackArea(this);
            ArrayList<Entity> hit = region.getEntitiesIntersecting(area);
            return hit.size() > 0 && hit.contains(target);
        }
        return false;
    }

    public void attack(Region region, Random random) {
        meleeAttack(weapon, random, region);
    }

    @Override
    public String toString() {
        return "Living{" + getClass().getName() +
                ", Name= " + name +
                ", Gender= " + gender +
                ", posX= " + posX +
                ", posY= " + posY +
                ", posZ= " + posZ +
                '}';
    }


    public void equipWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    public String getName() {
        return name;
    }

    @Override
    public boolean isPassableBy(Entity e) {
        return false;
    }

    @Override
    public boolean isPassableBy(movementTypes movementType) {
        return false;
    }

    public Gender getGender() {
        return gender;
    }
}
