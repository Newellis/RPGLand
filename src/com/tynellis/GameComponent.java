package com.tynellis;

import com.tynellis.Entities.NPC.NpcBase;
import com.tynellis.Entities.NPC.NpcQuestGiver;
import com.tynellis.Entities.Player;
import com.tynellis.Events.EventHandler;
import com.tynellis.Events.KeysEvent;
import com.tynellis.Events.TurnTrigger;
import com.tynellis.Events.WorldEvent;
import com.tynellis.Menus.MainMenu;
import com.tynellis.Menus.Menu;
import com.tynellis.Menus.PauseMenu;
import com.tynellis.Save.FileHandler;
import com.tynellis.Save.InvalidSaveException;
import com.tynellis.Save.SavedWorld;
import com.tynellis.Save.StoreLoad;
import com.tynellis.World.Area;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.input.KeyInput;
import com.tynellis.input.Keys;
import com.tynellis.input.MouseInput;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class GameComponent extends JPanel implements Runnable {
    public static JFrame frame;
    public static GameComponent active;
    public static final int GAME_WIDTH = 1024;
    public static final int GAME_HEIGHT = GAME_WIDTH * 3 / 4;
    private int height = GAME_HEIGHT;
    private int width = GAME_WIDTH;
    public static boolean DEBUG = false;

    private BufferedImage screenFrame;
    private boolean running;
    private int fps;

    private static MouseInput mouse = new MouseInput();
    private static Keys keys = new Keys();

    private GameState state = GameState.MENU;
    private Menu menu = new MainMenu(GAME_WIDTH, GAME_HEIGHT);
    private EventHandler eventHandler;
    private World world;
    private Player player;
    private static final int autoSaveTicks = 60 * 30; //ticks per sec * seconds between saves
    private int ticksToSave = autoSaveTicks;


    public static void main(String[] args) {
        active = new GameComponent();
        frame = new JFrame();
        frame.setContentPane(active);
        frame.addKeyListener(new KeyInput(keys));
        frame.addMouseListener(mouse);
        frame.pack();
        frame.setSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        active.start();
    }

    public void start() {
        running = true;
        Thread thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public GameComponent() {
        this.setSize(new Dimension(width, height));
    }

    @Override
    public void run() {
        double unprocessed = 0;
        int toTick = 0;
        long lastTime = System.nanoTime();
        int frames = 0;
        long lastTimer1 = System.currentTimeMillis();

        long lastRenderTime = System.nanoTime();
        int min = 999999999;
        int max = 0;
        double frameRate = 60;
        double nsPerTick = 1000000000.0 / frameRate;
        eventHandler = new EventHandler();
        eventHandler.addEvent(new TurnTrigger(new KeysEvent(keys), 1));

        while (running) {
            if (!frame.hasFocus()) {
                keys.release();
                mouse.releaseAll();
            }

            boolean shouldRender = false;

            unprocessed %= 5;//drops ticks that where missed so as to not lag for as long
            while (unprocessed >= 1) {
                toTick++;
                unprocessed -= 1;
            }

            int tickCount = toTick;
            if (toTick > 0 && toTick < 3) {
                tickCount = 1;
            }
            if (toTick > 20) {
                toTick = 20;
            }

            for (int i = 0; i < tickCount; i++) {
                toTick--;
                tick();
                shouldRender = true;
            }

            if (shouldRender) {
                frames++;
                Graphics g = getGraphics();

                render(g);

                long renderTime = System.nanoTime();
                int timePassed = (int) (renderTime - lastRenderTime);
                if (timePassed < min) {
                    min = timePassed;
                }
                if (timePassed > max) {
                    max = timePassed;
                }
                lastRenderTime = renderTime;
            }

            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                fps = frames;
                frames = 0;
            }
        }
    }


    private synchronized void render(Graphics g) {
        if (screenFrame == null || screenFrame.getWidth() != width || screenFrame.getHeight() != height) {
            screenFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics screen = screenFrame.getGraphics();
        screen.setColor(Color.BLACK);
        screen.fillRect(0, 0, width, height);
        //fill screen here
        if (state == GameState.SINGLE_PLAYER || state == GameState.IN_GAME_MENU || state == GameState.PAUSE_MENU) {
            world.render(screen, width, height, (int) ((player.getX() + 0.5) * Tile.WIDTH), (int) ((player.getY() + 0.5) * Tile.HEIGHT), player.getZ());
        } else if (state == GameState.MENU){
            menu.render(screen, width, height);
        }
        if (state == GameState.IN_GAME_MENU) {
            menu.render(screen, width, height);
        } else if (state == GameState.PAUSE_MENU){
            screen.setColor(new Color(0.0f,0.0f,0.0f,0.5f));
            screen.fillRect(0, 0, width, height);
            menu.render(screen, width, height);
        }
        screen.setColor(Color.WHITE);
        screen.drawString("FPS: " + fps, 10, 20);
        if (player != null) {
            screen.drawString("X,Y: " + player.getX() + ", " + player.getY(), 10, 70);
        }
        width = getWidth();
        height = getHeight();
        g.drawImage(screenFrame, 0, 0, null);
    }

    private void tick() {
        eventHandler.tick();
        if (keys.pause.wasReleased()){
            if (menu != null) {
                state = GameState.SINGLE_PLAYER;
                menu = null;
            } else {
                state = GameState.PAUSE_MENU;
                menu = new PauseMenu(width, height);
            }
        }
        if(state == GameState.SINGLE_PLAYER || state == GameState.IN_GAME_MENU) {
            //eventHandler.addEvent(new TurnTrigger(new WorldEvent(world), eventHandler.getCurrentTurn()));
            world.tick();
            if (ticksToSave == 0) {
                ticksToSave = autoSaveTicks;
                saveWorld();
            }
            ticksToSave--;
        } else if (state == GameState.MENU){
            menu.tick(this, mouse, width, height);
        } else if (state == GameState.PAUSE_MENU){
            menu.tick(this, mouse, width, height);
        }
        if (state == GameState.IN_GAME_MENU){
            menu.tick(this, mouse, width, height);
        }
        mouse.tick();
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void startGame(String name, long seed) {
        System.out.print("Loading...");
        world = new World(name, seed);
        int X = 42 * Area.WIDTH, Y = 55 * Area.HEIGHT, Z = 0;
        //todo find and set suitable spawn point
//        boolean suitable = false;
//        while (!suitable) {
//            X = world.get().nextInt(World.WIDTH * Area.WIDTH);
//            Y = world.getRand().nextInt(World.HEIGHT * Area.HEIGHT);
//            if (world.getTile(X, Y, 0).isPassableBy(Entity.movementTypes.Walking)){
//                suitable = true;
//            }
//        }
        //todo add player customization
        player = new Player(keys, NpcBase.getName(NpcBase.NpcGender.MALE, world.getRand()), X, Y, Z);
        NpcBase npc = new NpcQuestGiver(X - 3, Y + 2, Z, NpcBase.NpcGender.FEMALE, world.getRand());
        world.setHalfNumOfAreas(1 + (width / (Tile.WIDTH * Area.WIDTH)), 1 + (height / (Tile.HEIGHT * Area.HEIGHT)));
        world.setAreaOffset((int) (player.getX() + 0.5) / (Area.WIDTH) - 3, (int) (player.getY() + 0.5) / (Area.HEIGHT) - 4);
        world.loadAreas();
        world.addEntity(player);
        world.addEntity(npc);
        state = GameState.SINGLE_PLAYER;
        System.out.println("Done");
        //todo add some sort of loading screen
    }

    public void saveWorld() {
        String playerName = player.getName();
        StoreLoad.StorePlayer(player);
        world.removeEntity(player);
        world.saveLoadedAreas();
        StoreLoad.StoreWorld(world, playerName);
        world.addEntity(player);
        player.setKeys(keys);
    }

    public void loadWorld(String worldName) throws InvalidSaveException {
        FileHandler.setGameDir(worldName);
        SavedWorld save = StoreLoad.LoadWorld();
        if (save != null) {
            world = save.getWorld();
            player = StoreLoad.LoadPlayer(save.getPlayerName());
            assert player != null;
            player.setKeys(keys);
            world.setHalfNumOfAreas(1 + (width / (Tile.WIDTH * Area.WIDTH)), 1 + (height / (Tile.HEIGHT * Area.HEIGHT)));
            world.setAreaOffset((int) (player.getX() + 0.5) / (Area.WIDTH) - 3, (int) (player.getY() + 0.5) / (Area.HEIGHT) - 4);
            world.loadAreas();
            world.addEntity(player);
        } else {
            throw new InvalidSaveException();
        }
    }

    public void Quit() {
        System.exit(0);
    }
}
