package com.tynellis.Menus.MenuComponents;

import com.tynellis.Art.Sprite;
import com.tynellis.Art.SpriteSheet;
import com.tynellis.input.MouseInput;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Button extends MenuComponent{
    private List<ButtonListener> listeners;

    private SpriteSheet SHEET = new SpriteSheet("tempArt/lpc/submission_daneeklu 2/ui/scrollsandblocks.png", 96, 32, 1);
    protected String Word;
    private Sprite sprite;
    private boolean performClick = false;
    protected boolean works;

    public Button(GuiCompLocations x, int xOffset, GuiCompLocations y,int yOffset, String word, boolean on) {
        offsetX = xOffset;
        offsetY = yOffset;
        X = x;
        Y = y;
        bounds = new Rectangle(96, 32);
        over = new Rectangle(96, 32);
        this.Word = word;
        sprite = SHEET.getSprite(2);
        works = on;
    }

    @Override
    public void render(Graphics g, int width, int height) {
        super.render(g,width,height);
        g.drawImage(sprite.getStill(0), bounds.x, bounds.y, null);
        //Font font = new Font("arial", Font.BOLD, 20);
        //g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString(Word, bounds.x + 10, bounds.y + 20);
    }

    @Override
    public void tick(MouseInput mouseButtons) {
        if (works) {
            sprite = SHEET.getSprite(2);
            if (mouseButtons.mouseOver(over)) {
                if (mouseButtons.isReleased(1)) {
                    postClick();
                } else if (mouseButtons.isDown(1)) {
                    sprite = SHEET.getSprite(3);
                }
            }
            if (performClick) {
                if (listeners != null) {
                    for (ButtonListener listener : listeners) {
                        listener.buttonPressed(this);
                    }
                }
                performClick = false;
            }
        }
        else sprite = SHEET.getSprite(3);
    }

    public void addListener(ButtonListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<ButtonListener>();
        }
        listeners.add(listener);
    }

    public void postClick() {
        performClick = works;
    }

    public String getName() {
        return Word;
    }
    public void setName(String name) {
        Word = name;
    }
    public void setWorks(boolean on) {
        works = on;
    }
    public int getHeight() {
        return bounds.height;
    }
    public int getWidth() {
        return bounds.width;
    }
}
