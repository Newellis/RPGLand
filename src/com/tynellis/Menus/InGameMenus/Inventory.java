package com.tynellis.Menus.InGameMenus;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.GameComponent;
import com.tynellis.Menus.MenuComponents.Button;
import com.tynellis.Menus.MenuComponents.GuiCompLocations;
import com.tynellis.Menus.MenuComponents.InventorySlot;
import com.tynellis.World.Entities.ItemEntity;
import com.tynellis.World.Entities.Living.Player;
import com.tynellis.World.Items.Containers.Container;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.debug.Debug;
import com.tynellis.input.MouseInput;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Inventory extends InGameMenu {
    private SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/ui/scrollsandblocks.png", 64, 64, 352, 128, 1);
    private Container inventory;
    private Player player;
    private List<InventorySlot> slots = new ArrayList<InventorySlot>();
    protected ItemPile mouseItem;
    protected Point mouseLocation;
    private int xOffset, yOffset;
    protected List<Rectangle> bounds = new ArrayList<Rectangle>();

    public Inventory(Player player, int xOffset, int yOffset) {
        super();
        this.player = player;
        inventory = player.getInventory();
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemPile pile = inventory.getContents()[i];
            slots.add(new InventorySlot(pile, GuiCompLocations.CENTER, (-120) + ((60) * (i % 5)) + xOffset, GuiCompLocations.CENTER, (-88) + ((60) * (i / 5)) + yOffset));
        }
        over = new Rectangle();
        bounds.add(over);
    }

    public Inventory(Player player) {
        this(player, 0, 0);
    }

    @Override
    public void render(Graphics g, int width, int height) {
        over.setBounds((width / 2) - 155 + xOffset, (height / 2) - 194 + yOffset + 24, 64 * 5, 64 * 6);
        for (int i = 0; i < 6; i++) {
            int imgX = 1;
            if (i == 0) imgX = 0;
            if (i == 5) imgX = 2;
            for (int j = 0; j < 5; j++) {
                int imgY = 1;
                if (j == 0) imgY = 0;
                if (j == 4) imgY = 2;
                BufferedImage image = SHEET.getSprite(imgX).getStill(imgY);
                g.drawImage(image, (width / 2) + (image.getWidth() * j) - 155 + xOffset, (height / 2) + (image.getHeight() * i) - 194 + yOffset, null);
            }
        }
        super.render(g, width, height);
        for (InventorySlot slot : slots) {
            slot.render(g, width, height);
        }
        drawMouseItem(g);
        for (InventorySlot slot : slots) {
            slot.renderToolTip(g, mouseLocation);
        }
        if (GameComponent.debug.State() && GameComponent.debug.isType(Debug.Type.COLLISION)) {
            for (Rectangle bound : bounds) {
                g.setColor(Color.ORANGE);
                g.drawRect(bound.x, bound.y + 24, bound.width, bound.height);
            }
        }
    }

    protected void drawMouseItem(Graphics g) {
        if (mouseLocation != null) {
            if (mouseItem != null) {
                BufferedImage image = mouseItem.getItem().getImage();
                if (mouseItem.getSize() >= mouseItem.getItem().getMaxStackSize() / 2) {
                    g.drawImage(image, mouseLocation.x - (image.getWidth() / 6), mouseLocation.y - (2 * image.getHeight() / 15), null);
                    g.drawImage(image, mouseLocation.x + (image.getWidth() / 6), mouseLocation.y, null);
                    if (mouseItem.getSize() >= mouseItem.getItem().getMaxStackSize()) {
                        g.drawImage(image, mouseLocation.x, mouseLocation.y - (image.getHeight() / 3), null);
                    }
                } else {
                    g.drawImage(image, mouseLocation.x, mouseLocation.y, null);
                }
            }
        }
    }

    @Override
    public void tick(MouseInput mouseButtons) {
        super.tick(mouseButtons);
        mouseLocation = new Point(mouseButtons.getX(), mouseButtons.getY());
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setPile(inventory.getSlot(i).getPile());
            mouseItem = slots.get(i).tick(mouseButtons, mouseItem);
            ItemPile pile = slots.get(i).getPile();
            inventory.setSlot(i, pile);
        }
        if (mouseItem != null && mouseButtons.isReleased(1)) {
            boolean notContained = true;
            for (Rectangle bound : bounds) {
                notContained = !bound.contains(mouseLocation);
                if (!notContained) {
                    break;
                }
            }
            if (notContained) {
                world.getCurrentRegion().queueAdditionOfEntity(new ItemEntity(new ItemPile(mouseItem.getItem(), mouseItem.getSize()), world.getRand(), player.getX() - (Math.sin(player.getFacingAngle()) * 2), player.getY() - (Math.cos(player.getFacingAngle()) * 2), player.getZ()));
                mouseItem = null;
            }
        }
    }

    @Override
    public void buttonPressed(Button button) {
        System.out.print("test");
    }

    @Override
    public void closeMenu() {
        if (mouseItem != null) {
            world.getCurrentRegion().queueAdditionOfEntity(new ItemEntity(mouseItem, world.getRand(), player.getX(), player.getY(), player.getZ()));
        }
    }
}
