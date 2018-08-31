package com.tynellis.World.Entities.NPC;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteImage;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.GameComponent;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Humanoid;
import com.tynellis.World.Entities.NPC.AiTasks.Pathfinding.PathfinderAi;
import com.tynellis.World.Entities.damage.Damage;
import com.tynellis.World.Items.weapons.Sword;
import com.tynellis.World.Items.weapons.Weapon;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.debug.Debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class NpcBase extends Humanoid {
    public enum NpcGender {
        MALE,
        FEMALE,
        OTHER,
        BOTH,
    }
    private String name;
    protected transient SpriteSheet spriteSheet;
    protected transient Animation animation;
    protected transient SpriteSheet attackSheet;
    protected transient Animation attackAnimation;
    private NpcGender gender;
    protected NpcAi Ai = new NpcAi();
    protected PathfinderAi pathfinder = new PathfinderAi();


    private Weapon weapon = new Sword("Murder Town", 25, 5, 2);


    protected NpcBase(String name, int x, int y, int z, NpcGender gender) {
        super(x, y, z, 32, 32);
        this.name = name;
        this.gender = gender;
        setSprite(gender);
    }

    public NpcBase(int x, int y, int z, NpcGender gender, Random random) {
        this(getName(gender, random), x, y, z, gender);
        setLooking(random.nextInt(4));
    }

    public NpcBase(int x, int y, int z, Random random) {
        this(x, y, z, NpcGender.values()[random.nextInt(NpcGender.values().length)], random);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setSprite(gender);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    protected void setSprite(NpcGender gender) {
        if (gender == NpcGender.MALE) {
            spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
            attackSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_slash.png", 64, 64, 1);
        } else if (gender == NpcGender.FEMALE) {
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

    public static String getName(NpcGender gender, Random random) {
        InputStream names;
        if (gender == NpcGender.MALE) {
            names = GameComponent.class.getResourceAsStream("names/MaleNames.txt");
        } else if (gender == NpcGender.FEMALE) {
            names = GameComponent.class.getResourceAsStream("names/FemaleNames.txt");
        } else if (gender == NpcGender.OTHER) {
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

    public void tick(World world, List<Entity> near) {
        Ai.tick(world, this);
//        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.PATH)) {
//            moving = false;
//        }
        super.tick(world, near);
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
            g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (height * 1.5)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
            animation.tick();
        }
        if (!attacking) {
            attackAnimation.pause();
            attackAnimation.skipToFrame(0);
        } else {
            attackAnimation.play();
            System.out.println("attack with " + weapon.getName() + " frame " + attackAnimation.getFrameNum());
            if (attackAnimation.getFrameNum() == 5) {
                attacking = false;
            }
            frame = attackAnimation.getFrame();
            if (hurt) {
                frame = SpriteImage.Tint(frame, Damage.BLEED_COLOR);
            }
            g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (height * 1.5)) - (int) (3 * (posZ / 4.0) * Tile.HEIGHT), null);
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

        }
        super.render(g, xOffset, yOffset);
    }

    public PathfinderAi getPathfinder() {
        return pathfinder;
    }

    public boolean canHit(World world, Entity target) {
        if (weapon.canUse(world, this)) {

            Rectangle area = weapon.getAttackArea(this);
            ArrayList<Entity> hit = world.getEntitiesIntersecting(area);
            return hit.size() > 0 && hit.contains(target);
        }
        return false;
    }

    public void attack(World world) {
        meleeAttack(weapon, world);
    }

    @Override
    public String toString() {
        return "NPC{" + getClass().getName() +
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
}
