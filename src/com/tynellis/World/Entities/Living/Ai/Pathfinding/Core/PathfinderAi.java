package com.tynellis.World.Entities.Living.Ai.Pathfinding.Core;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Entities.Living.Ai.AiTask;
import com.tynellis.World.Entities.Living.Ai.FaceClosestAi;
import com.tynellis.World.Entities.Living.LivingEntity;
import com.tynellis.World.Nodes.Node;
import com.tynellis.World.world_parts.Regions.Region;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

public class PathfinderAi extends AiTask {
    protected double destX, destY, destZ;
    protected int range, minRange, tempMinRangeMod = 0;
    protected transient List<Node> path = new ArrayList<Node>();
    AiTask currentActivity;

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

    public PathfinderAi() {
        this(100, 0);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        path = new ArrayList<Node>();
    }

    public boolean performTask(Region region, Random random, LivingEntity entity) {
        if (canGetTo(entity, destX, destY, destZ)) {
            if ((path.size() == 0 || !pathIsValid(region, entity)) && (Math.abs(entity.getX() - destX) > entity.getSpeed() || Math.abs(entity.getY() - destY) > entity.getSpeed())) {
                if (findPath(region, entity, minRange + tempMinRangeMod)) {
                    tempMinRangeMod = 0;
                    return true;
                } else {
                    tempMinRangeMod++;
                    if (tempMinRangeMod > range / 2) {
                        tempMinRangeMod = 0;
                    }
                    return false;
                }
            } else {
                if (path.size() > 0 && region.getTile((int) path.get(0).getX(), (int) path.get(0).getY(), (int) path.get(0).getZ()) == null) {
                    entity.setMoving(false);
                    System.out.println("no next node");
                    return false;
                }
                return path.size() > 0 && moveAlongPath(region, random, entity);
            }
        }
        return false;
    }

    public boolean isFinished(LivingEntity entity) {
        return path.size() == 0;
    }

    protected boolean moveAlongPath(Region region, Random random, Entity e) {
        Node nextNode = path.get(0);
        if (path.size() == 1 && (Math.abs(e.getX() - nextNode.getX()) == 0 && Math.abs(e.getY() - nextNode.getY()) == 0)) { //at end of path
            e.setMoving(false);
            path.clear();
            return false;
        } else if ((Math.abs(e.getX() - nextNode.getX()) == 0 && Math.abs(e.getY() - nextNode.getY()) == 0)) { // at next node
            path.remove(nextNode);
            return true;
        } else if ((Math.abs(e.getX() - nextNode.getX()) < (e.getSpeed()) && Math.abs(e.getY() - nextNode.getY()) < (e.getSpeed()))) { //almost at next node
            e.setMoving(false);
            e.setLocation(nextNode.getX(), nextNode.getY(), nextNode.getZ());
            return true;
        } else { // move to next node
            if (region.isTileCurrentlyObstructedFor(e, (int) nextNode.getX(), (int) nextNode.getY(), (int) nextNode.getZ())) { // next node is currently obstructed
                e.setFacing(FaceClosestAi.facingPoint(e, nextNode.getX(), nextNode.getY()) + 0.75);
            } else {
                e.setFacing(FaceClosestAi.facingPoint(e, nextNode.getX(), nextNode.getY()));
            }
//            e.setFacing(FaceClosestAi.facingPoint(e, nextNode.getX(), nextNode.getY()));

            e.setMoving(true);
            return true;
        }
    }

    protected boolean pathIsValid(Region region, Entity e) {
        if (path.size() == 0) {
            System.out.println("no path");
            return false;
        }
        Node nextNode = path.get(0);
        if ((Math.abs(e.getX() - nextNode.getX()) > 1.1 + e.getSpeed() || Math.abs(e.getY() - nextNode.getY()) > 1.1 + e.getSpeed())) {
            return false;
        }
        for (int i = 1; i < path.size() - 1; i++) {
            Node node = path.get(i);
            if (region.getTile((int) node.getX(), (int) node.getY(), (int) node.getZ()) != null && region.isTileObstructed((int) node.getX(), (int) node.getY(), (int) node.getZ())) {
                System.out.println("node " + i + " is obstructed");
                return false;
            }
        }
        Node endNode = new Node(destX, destY, destZ);
        if (!isCloseToGoal(path.get(path.size() - 1), endNode, minRange + tempMinRangeMod)) {
            return false;
        }
        return true;
    }

    private boolean findPath(Region region, Entity e, int minRange) {
        List<Node> closedSet = new ArrayList<Node>();// The set of nodes already evaluated.
        List<Node> openSet = new ArrayList<Node>(); // The set of tentative nodes to be evaluated, initially containing the start node
        Node start = new Node(e.getX(), e.getY(), e.getZ(), region.getTile((int) Math.round(e.getX()), (int) Math.round(e.getY()), (int) Math.round(e.getZ())));
        Node goal = new Node(destX, destY, destZ, region.getTile((int) Math.round(destX), (int) Math.round(destY), (int) Math.round(destZ)));
        if (region.getTile((int) goal.getX(), (int) goal.getY(), (int) goal.getZ()) != null && (!region.getTile((int) goal.getX(), (int) goal.getY(), (int) goal.getZ()).isPassableBy(e) || region.isTileObstructed((int) goal.getX(), (int) goal.getY(), (int) goal.getZ()))) {
            if (minRange < 1) {
                return false;
            }
        }
        if (region.getTile((int) e.getX(), (int) e.getY(), (int) e.getZ()) != null && (!region.getTile((int) e.getX(), (int) e.getY(), (int) e.getZ()).isPassableBy(e) || region.isTileObstructed((int) e.getX(), (int) e.getY(), (int) e.getZ()))) {
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
        int nodesSearched = 0;
        while (openSet.size() > 0 && nodesSearched < range * 10) {
            Node current = null;
            for (Node node : openSet) {
                if (current == null) {
                    current = node;
                } else if (current.getFScore() > node.getFScore()) {
                    current = node;
                }
            }
            nodesSearched++;
            assert current != null;
            if (isCloseToGoal(current, goal, minRange)) {
                return reconstruct_path(cameFrom, current);
            }
            openSet.remove(current);
            closedSet.add(current);
            ArrayList<Node> nodes = region.getAdjacentNodes(current, e);
            for (Node node : nodes) {
                current.addNeighbor(node, e);
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
        System.out.println("didn't find path to " + destX + ", " + destY + ", " + destZ + " for " + currentActivity);
        return false;
    }

    private boolean isCloseToGoal(Node current, Node goal, int minRange) {
        return heuristicCostEstimate(current, goal) <= minRange;
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
    public double heuristicCostEstimate(Node pos, Node goal) {//todo get a better cost estimate
        double dx = Math.abs(pos.getX() - goal.getX());
        double dy = Math.abs(pos.getY() - goal.getY());
        double dz = Math.abs(pos.getZ() - goal.getZ());

        double D = 1, C = Math.sqrt(2), H = 1 / range;

        return (D * (dx + dy + dz) + (C - 2 * D) * Math.min(dx, dy)) * (1.0 + H);
    }

    public void setLocation(double x, double y, double z) {
        destX = x;
        destY = y;
        destZ = z;
    }

    public void setRanges(int range, int minRange) {
        this.range = range;
        this.minRange = minRange;
    }

    public AiTask getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(AiTask task) {
        if (currentActivity == null || currentActivity != task) {
            currentActivity = task;
            path.clear();
            tempMinRangeMod = 0;
        }
    }

    public void clearCurrentActivity() {
        setCurrentActivity(null);
    }

    public List<Node> getPath() {
        return path;
    }

    public void setPath(List<Node> path) {
        this.path = path;
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

    public boolean canGetTo(LivingEntity entity, double x, double y, double z) {
        return heuristicCostEstimate(new Node(entity.getX(), entity.getY(), entity.getZ()), new Node(x, y, z)) < range;
    }
}
