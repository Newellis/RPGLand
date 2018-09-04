package com.tynellis.Menus.InGameMenus;

import com.tynellis.Art.SpriteSheet;
import com.tynellis.Menus.MenuComponents.GuiCompLocations;
import com.tynellis.Menus.MenuComponents.InventorySlot;
import com.tynellis.World.Entities.Player;
import com.tynellis.World.Entities.UsableEntity.ChestInterface;
import com.tynellis.World.Items.ItemPile;
import com.tynellis.input.MouseInput;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ChestInventory extends Inventory {
    private SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/ui/scrollsandblocks.png", 32, 32, 96, 224, 1);
    private ChestInterface chestInterface;
    private List<InventorySlot> slots = new ArrayList<InventorySlot>();
    private Rectangle chestBouds = new Rectangle();

    public ChestInventory(ChestInterface chest, Player player) {
        super(player, 0, 130);
        chestInterface = chest;

        int slotsAcross = 10;

        for (int i = 0; i < chest.getSize(); i++) {
            ItemPile pile = chest.getItem(i).getPile();
            slots.add(new InventorySlot(pile, GuiCompLocations.CENTER, (-(int) (60 * (slotsAcross / 2.0)) + 30) + ((60) * (i % slotsAcross)), GuiCompLocations.CENTER, (-(60 * (chest.getSize() / slotsAcross))) + ((60) * (i / slotsAcross)) + 24));
        }
        bounds.add(chestBouds);
    }

    public void render(Graphics g, int width, int height) {
        chestBouds.setBounds((width / 2) + (-(int) (64 * (10 / 2.0))), (height / 2) + (-(64 * (4))), 64 * 10, 64 * 4 + 32);
        super.render(g, width, height);
        for (int i = 0; i < 9; i++) {
            int imgX = 1;
            if (i == 0) imgX = 0;
            if (i == 8) imgX = 2;
            for (int j = 0; j < 20; j++) {
                int imgY = 1;
                if (j == 0) imgY = 0;
                if (j == 19) imgY = 2;
                BufferedImage image = SHEET.getSprite(imgX).getStill(imgY);
                g.drawImage(image, chestBouds.x + (image.getWidth() * j), chestBouds.y + (image.getHeight() * i) - 24, null);
            }
        }
        for (InventorySlot slot : slots) {
            slot.render(g, width, height);
        }
        for (InventorySlot slot : slots) {
            slot.renderToolTip(g, mouseLocation);
        }
        drawMouseItem(g);
    }

    public void tick(MouseInput mouseButtons) {
        super.tick(mouseButtons);
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).setPile(chestInterface.getItem(i).getPile());
            mouseItem = slots.get(i).tick(mouseButtons, mouseItem);
            ItemPile pile = slots.get(i).getPile();
            chestInterface.setItem(i, pile);
        }
    }

    public void closeMenu() {
        super.closeMenu();
        chestInterface.stopUsing();
    }
}