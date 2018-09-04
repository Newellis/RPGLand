package com.tynellis.Menus.MenuComponents;

import com.tynellis.World.Items.ItemPile;
import com.tynellis.input.MouseInput;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class InventorySlot extends MenuComponent {

    private ItemPile item;
    private boolean mouseOver;

    public InventorySlot(ItemPile pile, GuiCompLocations x, int xOffset, GuiCompLocations y, int yOffset) {
        offsetX = xOffset;
        offsetY = yOffset;
        X = x;
        Y = y;
        bounds = new Rectangle(48, 48);
        over = new Rectangle(48, 48);
        item = pile;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        super.render(g, width, height);

        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.25f));
//        g.fillRect(bounds.x - (bounds.width / 4), bounds.y - (2 * (bounds.height / 5)), (int) (bounds.width * 1.5), (int) (bounds.height * 1.5));
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        if (item != null) {
            BufferedImage image = item.getItem().getImage();
            if (item.getSize() >= item.getItem().getMaxStackSize() / 2) {
                g.drawImage(image, bounds.x - (image.getWidth() / 6) + (bounds.width / 6), bounds.y - (2 * image.getHeight() / 15) + (4 * (bounds.height / 15)), null);
                g.drawImage(image, bounds.x + (image.getWidth() / 6) + (bounds.width / 6), bounds.y + (4 * (bounds.height / 15)), null);
                if (item.getSize() >= item.getItem().getMaxStackSize()) {
                    g.drawImage(image, bounds.x + (bounds.width / 6), bounds.y - (image.getHeight() / 3) + (4 * (bounds.height / 15)), null);
                }
            } else {
                g.drawImage(image, bounds.x + (bounds.width / 6), bounds.y + (4 * (bounds.height / 15)), null);
            }
        }
        if (mouseOver) {
            g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.25f));
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    @Override
    public void tick(MouseInput mouseButtons) {
        mouseOver = mouseButtons.mouseOver(over);
    }

    public ItemPile tick(MouseInput mouseButtons, ItemPile mouseItem) {
        tick(mouseButtons);
        if (mouseButtons.mouseOver(over) && mouseButtons.isReleased(1)) {
            ItemPile buffer = null;
            if (item != null) {
                buffer = new ItemPile(item.getItem(), item.getSize());
            }
            item = mouseItem;
            return buffer;
        }
        return mouseItem;
    }

    public void renderToolTip(Graphics g, Point mouseLocation) {
        if (mouseOver && item != null) {
            g.setColor(Color.BLACK);
            Font font = new Font("arial", Font.BOLD, 15);
            g.setFont(font);
            g.drawString(item.getItem().getName(), mouseLocation.x, mouseLocation.y - 30);
        }
    }

    public ItemPile getPile() {
        return item;
    }

    public void setPile(ItemPile pile) {
        item = pile;
    }
}
