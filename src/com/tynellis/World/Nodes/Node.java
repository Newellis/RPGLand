package com.tynellis.World.Nodes;

import com.tynellis.World.Tiles.Tile;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
    private List<NodeRelation> neighbors = new ArrayList<NodeRelation>();
    private double X, Y, Z;
    private double scoreFromStart = Double.MAX_VALUE;
    private double fScore;

    public Node(double x, double y, double z) {
        X=x;
        Y=y;
        Z = z;
    }

    public void render(Graphics g, int xOffset, int yOffset){
        if (Z < 1) {
            g.setColor(Color.MAGENTA);
        } else if (Z < 2) {
            g.setColor(Color.RED);
        } else if (Z < 3) {
            g.setColor(Color.ORANGE);
        }
        g.drawOval((int) (X * Tile.WIDTH) + xOffset, (int) (Y * Tile.HEIGHT) + yOffset - ((int) (3 * (Z / 4.0) * Tile.HEIGHT)), Tile.WIDTH, Tile.HEIGHT);
    }

    public List<NodeRelation> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(Node node) {
        int distance = (int) Math.sqrt(Math.pow(Math.abs(X - node.getX()), 2) + Math.pow(Math.abs(Y - node.getY()), 2));
        NodeRelation neighbor = new NodeRelation(node, distance);
        neighbors.add(neighbor);
    }

    @Override
    public String toString() {
        return "Node{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                '}';
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }

    public boolean setScore(double score) {
        if (score < scoreFromStart){
            scoreFromStart = score;
            return true;
        }
        return false;
    }

    public double getScore() {
        return scoreFromStart;
    }

    public void setFScore(double score) {
        fScore = score;
    }

    public double getFScore(){
        return fScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        return Double.compare(node.X, X) == 0 && Double.compare(node.Y, Y) == 0;
    }

    @Override
    public int hashCode() {
        return ((int)X)<<16 ^ (int)Y;
    }

    public class NodeRelation implements Serializable {
        private int distance;
        private Node node;
        public NodeRelation(Node node, int distance){
            this.node = node;
            this.distance = distance;
        }
        public int getDistance(){
            return distance;
        }
        public Node getNode() {
            return node;
        }
    }
}
