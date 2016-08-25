package com.tynellis.World.Nodes;

import com.tynellis.World.Tiles.Tile;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KeyNode {
    private List<KeyNodeRelation> neighbors = new ArrayList<KeyNodeRelation>();

    public KeyNode(double x, double y, double z) {
    }

    public void render(Graphics g, int x, int y) {
        g.setColor(Color.YELLOW);
        g.drawOval(x, y, Tile.WIDTH, Tile.HEIGHT);

//        for (KeyNodeRelation relation: getNeighbors()){
//            g.drawLine(x, y, (int) (relation.getKeyNode().getX() * Tile.WIDTH) + xOffset, (int) (relation.getKeyNode().getY() * Tile.HEIGHT) + yOffset);
//        }
    }

//    public void addNeighbor(KeyNode node) {
//        int distance = (int) Math.sqrt(Math.pow(Math.abs(X - node.getX()), 2) + Math.pow(Math.abs(Y - node.getY()), 2));
//        KeyNodeRelation neighbor = new KeyNodeRelation(node, distance);
//        neighbors.add(neighbor);
//    }
//
//    public Node getNodeFromKeyNode(int x, int y, int z) {
//        Node node = new Node(x,y,z);
//        for (KeyNodeRelation relation: neighbors) {
//            node.addNeighbor(((KeyNode)relation.getKeyNode()).getNodeFromKeyNode());
//        }
//        return node;
//    }

    public class KeyNodeRelation implements Serializable {
        private int distance;
        private KeyNode node;

        public KeyNodeRelation(KeyNode node, int distance) {
            this.node = node;
            this.distance = distance;
        }

        public int getDistance() {
            return distance;
        }

        public KeyNode getKeyNode() {
            return node;
        }
    }
}
