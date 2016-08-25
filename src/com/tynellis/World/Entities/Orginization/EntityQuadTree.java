package com.tynellis.World.Entities.Orginization;

import com.tynellis.World.Entities.Entity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class EntityQuadTree {
    private static final int threshold = 10, depth = 7;
    private int level;
    private SortedSet<Entity> entities;
    private EntityQuadTree[] nodes;
    private Rectangle bounds;

    public EntityQuadTree(int level, Rectangle bounds) {
        this.level = level;
        entities = new TreeSet<Entity>(new EntityComparator());
        this.bounds = bounds;
        nodes = new EntityQuadTree[4];
    }

    public void clear() {
        entities.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        int width = (int) (bounds.getWidth() / 2);
        int height = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();

        nodes[0] = new EntityQuadTree(level + 1, new Rectangle(x + width, y, width, height));
        nodes[1] = new EntityQuadTree(level + 1, new Rectangle(x, y, width, height));
        nodes[2] = new EntityQuadTree(level + 1, new Rectangle(x, y + height, width, height));
        nodes[3] = new EntityQuadTree(level + 1, new Rectangle(x + width, y + height, width, height));
    }

    private int getIndex(Rectangle pRect) {
        int index = -1;

        int width = (int) (bounds.getWidth() / 2);
        int height = (int) (bounds.getHeight() / 2);
        int x = (int) bounds.getX();
        int y = (int) bounds.getY();
        Rectangle topRight = new Rectangle(x + width, y, width, height);
        Rectangle topLeft = new Rectangle(x, y, width, height);
        Rectangle bottomLeft = new Rectangle(x, y + height, width, height);
        Rectangle bottomRight = new Rectangle(x + width, y + height, width, height);

        if (topLeft.contains(pRect)) {
            index = 1;
        } else if (bottomLeft.contains(pRect)) {
            index = 2;
        } else if (topRight.contains(pRect)) {
            index = 0;
        } else if (bottomRight.contains(pRect)) {
            index = 3;
        }
        return index;
    }

    public void insert(Entity entity) {
        if (nodes[0] != null) {
            int index = getIndex(entity.getBounds());
            if (index != -1) {//fits fully in one quadrant
                nodes[index].insert(entity);
                return;
            }
        }

        entities.add(entity);

        if (entities.size() > threshold && level < depth) {
            if (nodes[0] == null) {
                split();
            }

            Iterator<Entity> iterator = entities.iterator();
            while (iterator.hasNext()) {
                Entity entityTest = iterator.next();
                int index = getIndex(entityTest.getBounds());
                if (index != -1) {
                    nodes[index].insert(entityTest);
                    iterator.remove();
                }

            }
        }
    }

    public List<Entity> retrieve(List<Entity> returnObjects, Rectangle pRect) {
        int index = getIndex(pRect);

        if (index != -1 && nodes[index] != null) {
            nodes[index].retrieve(returnObjects, pRect);
        } else if (nodes[0] != null) {
            int halfWidth = (int) (bounds.getWidth() / 2);
            int halfHeight = (int) (bounds.getHeight() / 2);
            int x = (int) bounds.getX();
            int y = (int) bounds.getY();
            Rectangle top = new Rectangle(x, y, bounds.width, halfHeight);
            Rectangle bottom = new Rectangle(x, y + halfHeight, bounds.width, halfHeight);
            Rectangle left = new Rectangle(x, y, halfWidth, bounds.height);
            Rectangle right = new Rectangle(x + halfWidth, y, halfWidth, bounds.height);
            if (top.contains(pRect)) {
                nodes[0].retrieve(returnObjects, pRect);
                nodes[1].retrieve(returnObjects, pRect);
            } else if (bottom.contains(pRect)) {
                nodes[2].retrieve(returnObjects, pRect);
                nodes[3].retrieve(returnObjects, pRect);
            } else if (left.contains(pRect)) {
                nodes[1].retrieve(returnObjects, pRect);
                nodes[2].retrieve(returnObjects, pRect);
            } else if (right.contains(pRect)) {
                nodes[0].retrieve(returnObjects, pRect);
                nodes[3].retrieve(returnObjects, pRect);
            } else {
                nodes[0].retrieve(returnObjects, pRect);
                nodes[1].retrieve(returnObjects, pRect);
                nodes[2].retrieve(returnObjects, pRect);
                nodes[3].retrieve(returnObjects, pRect);
            }
        }

        if (returnObjects != null) {
            returnObjects.addAll(entities);
        } else {
            returnObjects = new ArrayList<Entity>();
            returnObjects.addAll(entities);
        }
        return returnObjects;
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        switch (level) {
            case 0:
                g.setColor(Color.RED);
                break;
            case 1:
                g.setColor(Color.ORANGE);
                break;
            case 2:
                g.setColor(Color.YELLOW);
                break;
            case 3:
                g.setColor(Color.GREEN);
                break;
            case 4:
                g.setColor(Color.BLUE);
                break;
            case 5:
                g.setColor(Color.MAGENTA);
                break;
            case 6:
                g.setColor(Color.CYAN);
                break;
            case 7:
                g.setColor(Color.BLACK);
                break;
        }
        g.drawRect(bounds.x + xOffset, bounds.y + yOffset, bounds.width, bounds.height);
        for (EntityQuadTree tree : nodes) {
            if (tree != null) {
                tree.render(g, xOffset, yOffset);
            }
        }
    }
}
