package com.tynellis.Entities.NPC;

import com.tynellis.Art.Animation;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.Entities.Entity;
import com.tynellis.Entities.Mob;
import com.tynellis.Entities.NPC.AiTasks.FaceClosestPlayerAi;
import com.tynellis.Entities.NPC.AiTasks.FollowEntityAi;
import com.tynellis.Entities.NPC.AiTasks.PathfinderAi;
import com.tynellis.Entities.Player;
import com.tynellis.GameComponent;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class NpcBase extends Mob {
    private String name;
    private transient SpriteSheet spriteSheet;
    private transient Animation animation;
    private NpcGender gender;
    private NpcAi Ai = new NpcAi();
    private PathfinderAi pathfinder;
    public enum NpcGender {
        MALE,
        FEMALE,
        OTHER,
        BOTH,
    }

    private NpcBase(String name, int x, int y, int z, NpcGender gender) {
        super(x, y, z, 32, 32);
        this.name = name;
        this.gender = gender;
        pathfinder = new FollowEntityAi(Player.class, 30, 2);//new PathfinderAi(x, y, z, 48);
        Ai.addTask(0, pathfinder);
        Ai.addTask(1, new FaceClosestPlayerAi(2));
        setSprite(gender);
    }

    public NpcBase(int x, int y, int z, NpcGender gender, Random random) {
        this(getName(gender, random), x, y, z, gender);
        setSprite(gender);
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        setSprite(gender);
        animation = new Animation(spriteSheet, 5);
        animation.playInRange(spriteFacing, 1, 8);
    }
    private void setSprite(NpcGender gender) {
        if (gender == NpcGender.MALE) {
            spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
        } else if (gender == NpcGender.FEMALE) {
            spriteSheet = new SpriteSheet("tempArt/lpc/core/char/female/female_walkcycle.png", 64, 64, 1);
        } else {
            spriteSheet = new SpriteSheet("tempArt/lpc/core/char/male/male_walkcycle.png", 64, 64, 1);
        }
        animation = new Animation(spriteSheet, 5);
        animation.playInRange(spriteFacing, 1, 8);
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

    public void tick(World world) {
        Ai.tick(world, this);
        super.tick(world);
        animation.setRow(spriteFacing);
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        BufferedImage frame;
        if (!moving) {
            animation.pause();
            animation.skipToFrame(0);
        } else {
            animation.play();
        }
        frame = animation.getFrame();
        g.drawImage(frame, (int) ((posX + 0.5) * Tile.WIDTH) + xOffset - (frame.getWidth() / 2), (int) (((posY + 0.5) * Tile.HEIGHT) + yOffset - (height * 1.5)), null);
        animation.tick();
        if (World.DEBUG) {
            List<Node> nodes = pathfinder.getPath();
            if (nodes != null) {
                for (Node node : nodes) {
                    node.render(g, xOffset, yOffset);
                }
            }
        }
        super.render(g, xOffset, yOffset);
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
