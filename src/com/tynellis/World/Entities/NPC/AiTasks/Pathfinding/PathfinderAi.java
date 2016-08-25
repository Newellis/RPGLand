package com.tynellis.World.Entities.NPC.AiTasks.Pathfinding;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.KillableEntity;
import com.tynellis.World.Entities.NPC.AiTasks.AiTask;
import com.tynellis.World.Entities.NPC.AiTasks.FaceClosestAi;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.World;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathfinderAi extends AiTask implements Serializable {
    protected double destX, destY, destZ;
    protected int range, minRange;
    protected transient List<Node> path = new ArrayList<Node>();

    public PathfinderAi(int x, int y, int z, int range) {
        destX = x;
        destY = y;
        destZ = z;
        this.range = range;
    }

    public PathfinderAi(int range, int minRange) {
        this.range = range;
        this.minRange = minRange;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        path = new ArrayList<Node>();
    }

    public boolean performTask(World world, KillableEntity entity) {
        if ((path.size() == 0 || !pathIsValid(world, entity)) && (Math.abs(entity.getX() - destX) > entity.getSpeed() || Math.abs(entity.getY() - destY) > entity.getSpeed())) {
            System.out.println("find path");
            return findPath(world, entity);
        } else {
            if (path.size() > 0 && world.getTile((int) path.get(0).getX(), (int) path.get(0).getY(), (int) path.get(0).getZ()) == null) {
                entity.setMoving(false);
                return false;
            }
            return path.size() > 0 && moveAlongPath(entity);
        }
    }

    public boolean isFinished() {
        return path.size() == 0;
    }

    protected boolean moveAlongPath(Entity e) {
        Node nextNode = path.get(0);
        if (path.size() == 1 && (Math.abs(e.getX() - nextNode.getX()) == 0 && Math.abs(e.getY() - nextNode.getY()) == 0)) {
            e.setMoving(false);
            path.clear();
            return false;
        } else if ((Math.abs(e.getX() - nextNode.getX()) == 0 && Math.abs(e.getY() - nextNode.getY()) == 0)) {
            path.remove(nextNode);
            return true;
        } else if ((Math.abs(e.getX() - nextNode.getX()) < (e.getSpeed()) && Math.abs(e.getY() - nextNode.getY()) < (e.getSpeed()))) {
            e.setMoving(false);
            e.setLocation(nextNode.getX(), nextNode.getY(), nextNode.getZ());
            return true;
        } else {
            e.setFacing(FaceClosestAi.facingPoint(e, nextNode.getX(), nextNode.getY()));
            e.setMoving(true);
            return true;
        }
    }

    protected boolean pathIsValid(World world, Entity e) {
        if (path.size() == 0) {
            return false;
        }
        Node nextNode = path.get(0);
        if ((Math.abs(e.getX() - nextNode.getX()) > 1.1 + e.getSpeed() || Math.abs(e.getY() - nextNode.getY()) > 1.1 + e.getSpeed())) {
            return false;
        }
        for (int i = 1; i < path.size() - 1; i++) {
            Node node = path.get(i);
            if (world.getTile((int) node.getX(), (int) node.getY(), (int) node.getZ()) != null && world.isTileObstructed((int) node.getX(), (int) node.getY(), (int) node.getZ())) {
                return false;
            }
        }
        Node endNode = new Node(destX, destY, destZ);
        if (!path.get(path.size() - 1).equals(endNode)) {
            return false;
        }
        return true;
    }

    private boolean findPath(World world, Entity e) {
        List<Node> closedSet = new ArrayList<Node>();// The set of nodes already evaluated.
        List<Node> openSet = new ArrayList<Node>(); // The set of tentative nodes to be evaluated, initially containing the start node
        Node start = new Node(e.getX(), e.getY(), e.getZ());
        Node goal = new Node(destX, destY, destZ);
        if (world.getTile((int) goal.getX(), (int) goal.getY(), (int) goal.getZ()) != null && (!world.getTile((int) goal.getX(), (int) goal.getY(), (int) goal.getZ()).isPassableBy(e) || world.isTileObstructed((int) goal.getX(), (int) goal.getY(), (int) goal.getZ()))) {
            return false;
        }
        if (world.getTile((int) e.getX(), (int) e.getY(), (int) e.getZ()) != null && (!world.getTile((int) e.getX(), (int) e.getY(), (int) e.getZ()).isPassableBy(e) || world.isTileObstructed((int) e.getX(), (int) e.getY(), (int) e.getZ()))) {
            return false;
        }
        openSet.add(start);
        Map<Node, Node> cameFrom = new HashMap<Node, Node>();// The map of navigated nodes.
        start.setScore(0); // Cost from start along best known path.
        // Estimated total cost from start to goal through y.
        start.setFScore(start.getScore() + heuristicCostEstimate(start, goal));
        if (start.getFScore() > range) {
            return false;
        }
        while (openSet.size() > 0) {
            if (cameFrom.size() > range * 2) {
                System.out.println("path is to long");
                return false;
            }
            Node current = null;
            for (Node node : openSet) {
                if (current == null) {
                    current = node;
                } else if (current.getFScore() > node.getFScore()) {
                    current = node;
                }
            }
            assert current != null;

            if (current.equals(goal)) {
                System.out.println("found path");
                return reconstruct_path(cameFrom, goal);
            }
            openSet.remove(current);
            closedSet.add(current);
            ArrayList<Node> nodes = world.getAdjacentNodes(current, e);
            for (Node node : nodes) {
                current.addNeighbor(node);
            }
            for (Node.NodeRelation nodeRelation : current.getNeighbors()) {
                Node neighbor = nodeRelation.getNode();
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                double tentativeGScore = current.getScore() + nodeRelation.getDistance();
                if (!openSet.contains(neighbor) || tentativeGScore < neighbor.getScore()) {
                    cameFrom.put(neighbor, current);
                    neighbor.setScore(tentativeGScore);
                    neighbor.setFScore(neighbor.getScore() + heuristicCostEstimate(neighbor, goal));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        System.out.println("didn't find path");
        return false;
    }

    private boolean reconstruct_path(Map<Node, Node> came_from, Node current) {
        path = new ArrayList<Node>();
        path.add(current);
        while (came_from.containsKey(current)) {
            current = came_from.get(current);
            path.add(0, current);
        }
        return getPathLength() < range;
    }

    //uses as strait of a line as can be done using 8 directions to calculate
    protected double heuristicCostEstimate(Node pos, Node goal) {//todo get a better cost estimate
        double x, y, z, scoreLeft = 0;
        x = Math.abs(pos.getX() - goal.getX());
        y = Math.abs(pos.getY() - goal.getY());
        z = Math.abs(pos.getZ() - goal.getZ());
        if (x + y + z == 0) {
            return scoreLeft;
        }
        if (x < y) {
            scoreLeft += y - x;
            scoreLeft += Math.sqrt(Math.pow(x, 2) + Math.pow(x, 2));
            scoreLeft += z;
        } else if (y < x) {
            scoreLeft += x - y;
            scoreLeft += Math.sqrt(Math.pow(y, 2) + Math.pow(y, 2));
            scoreLeft += z;
        } else {
            scoreLeft += Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            scoreLeft += z;
        }
        return scoreLeft;
    }

    public void setLocation(double x, double y, double z) {
        destX = x;
        destY = y - 0.5;
        destZ = z;
    }

    public List<Node> getPath() {
        return path;
    }

    public double getPathLength() {
        double length = 0.0;
        if (path.size() > 1) {
            for (int i = 1; i < path.size(); i++) {
                length += heuristicCostEstimate(path.get(i - 1), path.get(i));
            }
        }
        return length;
    }

}
