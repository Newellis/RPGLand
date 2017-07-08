package com.tynellis;

import com.tynellis.Events.EventHandler;
import com.tynellis.Events.TurnTrigger;
import com.tynellis.Events.WorldTickEvent;
import com.tynellis.Menus.InGameMenus.PauseMenu;
import com.tynellis.Menus.MainMenu;
import com.tynellis.Menus.Menu;
import com.tynellis.Save.FileHandler;
import com.tynellis.Save.InvalidSaveException;
import com.tynellis.Save.SavedWorld;
import com.tynellis.Save.StoreLoad;
import com.tynellis.World.Area;
import com.tynellis.World.Entities.ItemEntity;
import com.tynellis.World.Entities.NPC.LumberJackNpc;
import com.tynellis.World.Entities.NPC.NpcBase;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Entities.Tree;
import com.tynellis.World.Entities.UsableEntity.Chest;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.World.Items.Materials.Log;
import com.tynellis.World.Tiles.LandTiles.ManMade.Ladder;
import com.tynellis.World.Tiles.LandTiles.Natural.Grass;
import com.tynellis.World.Tiles.LandTiles.Natural.Slope;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.World;
import com.tynellis.input.KeyInput;
import com.tynellis.input.Keys;
import com.tynellis.input.MouseInput;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.util.Random;

public class GameComponent implements Runnable {
    public static JFrame frame;
    public static GameComponent active;
    public static GameRenderer renderer;
    public static Debug debug = new Debug();

    public static final int GAME_WIDTH = 1024;
    public static final int GAME_HEIGHT = GAME_WIDTH * 3 / 4;
    private int height = GAME_HEIGHT;
    private int width = GAME_WIDTH;

    private boolean running;

    private static MouseInput mouse = new MouseInput();
    private static Keys keys = new Keys();

    private GameState state = GameState.MENU;
    private Menu menu = new MainMenu(GAME_WIDTH, GAME_HEIGHT);
    public static World world;
    private static EventHandler events;
    private Player player;
    private static final int autoSaveTicks = 60 * 30; //ticks per sec * seconds between saves
    private int ticksToSave = autoSaveTicks;


    public static void main(String[] args) {
        active = new GameComponent();
        renderer = new GameRenderer(active);
        frame = new JFrame();
        frame.setContentPane(renderer);
        frame.addKeyListener(new KeyInput(keys));
        frame.addMouseListener(mouse);
        frame.pack();
        frame.setSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        active.start();
    }

    public void start() {
        running = true;
        Thread thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        Thread renderThread = new Thread(renderer);
        renderThread.setPriority(Thread.MAX_PRIORITY);
        renderThread.start();
    }

    @Override
    public void run() {
        double unprocessed = 0;
        int toTick = 0;
        long lastTime = System.nanoTime();

        double nsPerTick = 1000000000.0 / 60;
        System.out.println("tick Start");
        events = new EventHandler();
        while (running) {
            if (!frame.hasFocus()) {
                keys.release();
                mouse.releaseAll();
            }

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
            }

            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void draw() {
        frame.invalidate();
    }

    private void tick() {
        keys.tick();
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
            events.tick();
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
        System.out.print("Loading...");//todo change detail text on loading screen as loading
        world = new World(name, seed, events, this);
        world.genSpawn(seed);
        int[] spawn = world.getSpawnPoint();

        //todo add player customization
        player = new Player(keys, NpcBase.getName(NpcBase.NpcGender.MALE, world.getRand()), spawn[0], spawn[1], spawn[2]);
        world.addEntity(player);
        renderer.setWorld(world);
        renderer.setPlayer(player);

        //test stuff for testing purposes
        Chest chest = new Chest(spawn[0] - 5, spawn[1] + 2, spawn[2]);
        NpcBase npc = new LumberJackNpc(spawn[0] - 3, spawn[1] + 2, spawn[2], NpcBase.NpcGender.FEMALE, chest, world.getRand());
        ItemEntity itemEntity = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 1), world.getRand(), spawn[0] + 4, spawn[1], spawn[2]);
        ItemEntity itemEntity1 = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 10), world.getRand(), spawn[0] + 6, spawn[1], spawn[2]);
        ItemEntity itemEntity2 = new ItemEntity(new ItemPile(new Log(Tree.Type.Oak), 20), world.getRand(), spawn[0] + 8, spawn[1], spawn[2]);
        world.addEntity(npc);
        world.addEntity(itemEntity);
        world.addEntity(itemEntity1);
        world.addEntity(itemEntity2);
        world.addEntity(chest);
        //addTestStructure(world, spawn);


        System.out.println("Done");
        saveWorld();
        events.addEvent(new TurnTrigger(new WorldTickEvent(world)));
        state = GameState.SINGLE_PLAYER;
    }

    private void addTestStructure(World world, int[] spawn) {
        Random random = new Random();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                world.setTile(new Grass(random, 100), spawn[0] + i, spawn[1] - 10 + j, spawn[2] + 1);
                world.setTile(null, spawn[0] + i, spawn[1] - 10 + j, spawn[2]);

                world.setTile(new Grass(random, 100), spawn[0] + i - 7, spawn[1] - 10 + j, spawn[2] + 1);
                world.setTile(null, spawn[0] + i - 7, spawn[1] - 10 + j, spawn[2]);
            }
        }
        world.setTile(new Ladder(random, 100, 0, world.getTile(spawn[0], spawn[1] - 10 + 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0], spawn[1] - 10 + 2, spawn[2]);
        world.setTile(new Ladder(random, 100, 1, world.getTile(spawn[0] + 2, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] + 2, spawn[1] - 10, spawn[2]);
        world.setTile(new Ladder(random, 100, 2, world.getTile(spawn[0], spawn[1] - 10 - 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0], spawn[1] - 10 - 2, spawn[2]);
        world.setTile(new Ladder(random, 100, 3, world.getTile(spawn[0] - 2, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 2, spawn[1] - 10, spawn[2]);

        world.setTile(new Slope(random, 100, 0, world.getTile(spawn[0] - 7, spawn[1] - 10 + 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 7, spawn[1] - 10 + 2, spawn[2]);
        world.setTile(new Slope(random, 100, 1, world.getTile(spawn[0] + 2 - 7, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] + 2 - 7, spawn[1] - 10, spawn[2]);
        world.setTile(new Slope(random, 100, 2, world.getTile(spawn[0] - 7, spawn[1] - 10 - 2, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 7, spawn[1] - 10 - 2, spawn[2]);
        world.setTile(new Slope(random, 100, 3, world.getTile(spawn[0] - 2 - 7, spawn[1] - 10, spawn[2]), spawn[2] + 1, spawn[2]), spawn[0] - 2 - 7, spawn[1] - 10, spawn[2]);
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
            world.setEventHandler(events);
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
        running = false;
        GameRenderer.running = false;
        System.exit(0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public GameState getState() {
        return state;
    }

    public Menu getMenu() {
        return menu;
    }

    public EventHandler getEvents() {
        return events;
    }
}
