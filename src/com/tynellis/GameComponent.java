package com.tynellis;

import com.tynellis.Menus.InGameMenus.InGameMenu;
import com.tynellis.Menus.InGameMenus.PauseMenu;
import com.tynellis.Menus.MainMenu;
import com.tynellis.Menus.Menu;
import com.tynellis.Save.FileHandler;
import com.tynellis.Save.InvalidSaveException;
import com.tynellis.Save.SavedWorld;
import com.tynellis.Save.StoreLoad;
import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Entities.Living.Player;
import com.tynellis.World.Tiles.LandTiles.ManMade.Ladder;
import com.tynellis.World.Tiles.LandTiles.Natural.Grass;
import com.tynellis.World.Tiles.LandTiles.Natural.Slope;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.World.world_parts.Area;
import com.tynellis.World.world_parts.Regions.Region;
import com.tynellis.debug.Debug;
import com.tynellis.input.KeyInput;
import com.tynellis.input.Keys;
import com.tynellis.input.MouseInput;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GameComponent extends JPanel implements Runnable {
    public static JFrame frame;
    public static GameComponent active;
    public static final int GAME_WIDTH = 1024;
    public static final int GAME_HEIGHT = GAME_WIDTH * 3 / 4;
    private int height = GAME_HEIGHT;
    private int width = GAME_WIDTH;
    public static Debug debug = new Debug();

    private BufferedImage screenFrame;
    private boolean running;
    private int fps;

    private static MouseInput mouse = new MouseInput();
    private static Keys keys = new Keys();

    private GameState state = GameState.MENU;
    private Menu menu = new MainMenu(GAME_WIDTH, GAME_HEIGHT);
    public static World world;
    private static Player player;
    private static final int autoSaveTicks = 60 * 30; //ticks per sec * seconds between saves
    private int ticksToSave = autoSaveTicks;


    public static void main(String[] args) {
        active = new GameComponent();
        frame = new JFrame();
        frame.setContentPane(active);
        frame.addKeyListener(new KeyInput(keys));
        frame.addMouseListener(mouse);
        frame.addMouseMotionListener(mouse);
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
//        Thread renderer = new Thread(new GameRenderer());
//        renderer.setPriority(Thread.MAX_PRIORITY);
//        renderer.start();
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

        while (running) {
            if (!frame.hasFocus()) {
                keys.release();
                mouse.releaseAll();
            }

            boolean shouldRender = false;

            if (unprocessed > 5) {
                System.out.println("Is the system behind skipping " + (unprocessed / 20.0) + " seconds to catch up");
            }
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

                repaint();

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
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
            world.render(screen, width, height, (int) ((player.getX() + 0.5) * Tile.WIDTH), (int) ((player.getY() + 0.5) * Tile.HEIGHT), (int) (player.getZ() * (Tile.HEIGHT * 3 / 4)));
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
        Font font = new Font("arial", Font.BOLD, 10);
        screen.setFont(font);
        screen.drawString("FPS: " + fps, 10, 20);
        width = getWidth();
        height = getHeight();
        g.drawImage(screenFrame, 0, 0, null);
    }

    private void tick() {
        keys.tick();
        if (keys.pause.wasReleased()){
            if (menu != null) {
                state = GameState.SINGLE_PLAYER;
                if (menu instanceof InGameMenu) {
                    ((InGameMenu) menu).closeMenu();
                    player.removeMenu();
                }
                menu = null;
            } else {
                state = GameState.PAUSE_MENU;
                menu = new PauseMenu(width, height);
            }
        }
        if(state == GameState.SINGLE_PLAYER || state == GameState.IN_GAME_MENU) {
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
            if (menu instanceof InGameMenu) {
                ((InGameMenu) menu).tick(this, mouse, width, height, world);
            }
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
        world.genSpawn(seed);
        int[] spawn = world.getSpawnPoint();
        //todo add player customization
        player = new Player(keys, LivingEntity.getName(LivingEntity.Gender.MALE, world.getRand()), LivingEntity.Gender.MALE, spawn[0], spawn[1], spawn[2]);
        world.addPlayer(player);

        world.addTestEntities();
        //addTestStructure(world, spawn);

        state = GameState.SINGLE_PLAYER;
        System.out.println("Done");
        saveWorld();
        //todo add some sort of loading screen
    }

    private void addTestStructure(Region region, int[] spawn) {
        Random random = new Random();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                region.setTile(new Grass(random, 100), spawn[0] + i, spawn[1] - 10 + j, spawn[2] + 1);
                region.setTile(null, spawn[0] + i, spawn[1] - 10 + j, spawn[2]);

                region.setTile(new Grass(random, 100), spawn[0] + i - 7, spawn[1] - 10 + j, spawn[2] + 1);
                region.setTile(null, spawn[0] + i - 7, spawn[1] - 10 + j, spawn[2]);
            }
        }
        region.setTile(new Ladder(random, 100, 0, region.getTile(spawn[0], spawn[1] - 10 + 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0], spawn[1] - 10 + 2, spawn[2]);
        region.setTile(new Ladder(random, 100, 1, region.getTile(spawn[0] + 2, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] + 2, spawn[1] - 10, spawn[2]);
        region.setTile(new Ladder(random, 100, 2, region.getTile(spawn[0], spawn[1] - 10 - 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0], spawn[1] - 10 - 2, spawn[2]);
        region.setTile(new Ladder(random, 100, 3, region.getTile(spawn[0] - 2, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 2, spawn[1] - 10, spawn[2]);

        region.setTile(new Slope(random, 100, 0, region.getTile(spawn[0] - 7, spawn[1] - 10 + 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 7, spawn[1] - 10 + 2, spawn[2]);
        region.setTile(new Slope(random, 100, 1, region.getTile(spawn[0] + 2 - 7, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] + 2 - 7, spawn[1] - 10, spawn[2]);
        region.setTile(new Slope(random, 100, 2, region.getTile(spawn[0] - 7, spawn[1] - 10 - 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 7, spawn[1] - 10 - 2, spawn[2]);
        region.setTile(new Slope(random, 100, 3, region.getTile(spawn[0] - 2 - 7, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 2 - 7, spawn[1] - 10, spawn[2]);
    }

    //todo fix save load
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

    public static boolean isMainPlayer(Entity p) {
        return p.equals(player);
    }

    public void Quit() {
        System.exit(0);
    }
}
