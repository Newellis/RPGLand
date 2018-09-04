package com.tynellis.input;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener{

    private boolean[] currentState = new boolean[4];
    private boolean[] nextState = new boolean[4];

    private int x;
    private int y;
    private int curX;
    private int curY;
    private boolean mouseMoved;

    public MouseInput() {
    }

    public void setNextState(int button, boolean value) {
        nextState[button] = value;
    }

    public boolean isDown(int button) {
        return currentState[button];
    }

    public boolean isPressed(int button) {
        return !currentState[button] && nextState[button];
    }

    public boolean isReleased(int button) {
        return currentState[button] && !nextState[button];
    }

    public void tick() {
        System.arraycopy(nextState, 0, currentState, 0, currentState.length);
    }

    public void releaseAll() {
        for (int i = 0; i < nextState.length; i++) {
            nextState[i] = false;
        }
    }

    public void setPosition(Point mousePosition) {
        if (mousePosition != null) {
            x = mousePosition.x;
            y = mousePosition.y;
        }
    }

    public void setCurPosition(Point mousePosition) {
        if (mousePosition != null) {
            curX = mousePosition.x;
            curY = mousePosition.y;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCurX() {
        return curX;
    }

    public int getCurY() {
        return curY;
    }

    public boolean mouseOver(Rectangle rect) {
        Point mousePosition = new Point(x, y);
        return rect.contains(mousePosition);
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        setPosition(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        setNextState(e.getButton(), true);
        setPosition(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setNextState(e.getButton(), false);
        setPosition(e.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setPosition(e.getPoint());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setPosition(e.getPoint());
        releaseAll();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved = true;
        setPosition(e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseMoved = true;
        setPosition(e.getPoint());
    }
}
