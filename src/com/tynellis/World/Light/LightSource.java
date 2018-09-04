package com.tynellis.World.Light;

import com.tynellis.World.Entities.Entity;
import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class LightSource implements Serializable {
    private double x, y, z, range;
    private Polygon shape;
    transient SortedSet<Line2D> rays;

    public LightSource(double x, double y, double z, double range) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.range = range;
    }

    public LightSource(double range) {
        this.range = range;
    }

    public void render(Graphics g, int xOffset, int yOffset) {

//        Rectangle rectangle = getBounds();
//        g.setColor(Color.RED);
//        g.drawRect(rectangle.x + xOffset, rectangle.y + yOffset, rectangle.width, rectangle.height);
//
//        int r = 0;
//        if (rays != null) {
////            System.out.print("rays: ");
////            for (Line2D line : rays) {
////                System.out.print(/*Math.atan*/((line.getY2() - line.getY1()) / (line.getX2() - line.getX1())) + ", ");
////            }
////            System.out.print("\n");
//            Polygon shape = new Polygon();
//            for (Line2D ray : rays) {
//                g.setColor(new Color(r, 0, 0));
//                shape.addPoint((int) (ray.getX2() + xOffset), (int) (ray.getY2() + yOffset));
//                g.drawLine((int) (ray.getX1() + xOffset), (int) (ray.getY1() + yOffset), (int) ray.getX2() + xOffset, (int) ray.getY2() + yOffset);
//                r = (r + 20) % 256;
//            }
//            g.setColor(Color.BLUE);
//            g.drawPolygon(shape);
//        }
    }

    public void tick(Region region, List<Entity> near) {
        rays = new TreeSet<Line2D>(new RayComparator());
        for (Entity e : near) {
            Rectangle eBounds = e.getBounds();
            if (getBounds().intersects(eBounds)) {
                getLinesFromBounds(eBounds, near);
            }
        }
        getLinesFromBounds(getBounds(), near);
    }

    private void getLinesFromBounds(Rectangle eBounds, List<Entity> near) {
        Line2D.Double line = new Line2D.Double(x * Tile.WIDTH, y * Tile.HEIGHT, eBounds.getX(), eBounds.getY());
        addLine(line, eBounds, near);
        line = new Line2D.Double(x * Tile.WIDTH, y * Tile.HEIGHT, eBounds.getX() + eBounds.getWidth(), eBounds.getY());
        addLine(line, eBounds, near);
        line = new Line2D.Double(x * Tile.WIDTH, y * Tile.HEIGHT, eBounds.getX() + eBounds.getWidth(), eBounds.getY() + eBounds.getHeight());
        addLine(line, eBounds, near);
        line = new Line2D.Double(x * Tile.WIDTH, y * Tile.HEIGHT, eBounds.getX(), eBounds.getY() + eBounds.getHeight());
        addLine(line, eBounds, near);
    }

    private void addLine(Line2D line, Rectangle eBounds, List<Entity> near) {
        if (!line.getBounds().intersects(eBounds)) {
            //rays.add(line);
            double xDist = (x * Tile.WIDTH) - line.getX2();
            double yDist = (y * Tile.HEIGHT) - line.getY2();
            double ratio = (range / 1.4 * Tile.WIDTH) / (Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2)));
            //Line2D rangeLine = new Line2D.Double(x * Tile.WIDTH, y * Tile.HEIGHT, (x * Tile.WIDTH) - ((xDist) * ratio), (y * Tile.HEIGHT) - ((yDist) * ratio));
            ArrayList<Point2D> points = new ArrayList<Point2D>();
            for (Entity e : near) {
                Point2D newEnd = findIntersection(line, e.getBounds());
                if (newEnd != null) {
                    points.add(newEnd);
                }
            }
//            Point2D newEnd = findIntersection(line, getBounds());
//            if (newEnd != null) {
//                points.add(newEnd);
//            }
            Point2D point = findShortLine(points, line.getP1());
            if (point != null) {
                rays.add(new Line2D.Double(line.getP1(), point));
            } else {
                rays.add(line);
            }
            //rangeLine = new Line2D.Double(x * Tile.WIDTH, y * Tile.HEIGHT, (x * Tile.WIDTH) - ((xDist - 0.1) * ratio), (y * Tile.HEIGHT) - ((yDist - 0.1) * ratio));
            //rangeLine = new Line2D.Double(x * Tile.WIDTH, y * Tile.HEIGHT, (x * Tile.WIDTH) - ((xDist + 0.1) * ratio), (y * Tile.HEIGHT) - ((yDist + 0.1) * ratio));
        }
    }

    private Point2D findIntersection(Line2D line, Rectangle bounds) {
        double x1, x2, y1, y2;
        x1 = bounds.getX();
        x2 = bounds.getX() + bounds.getWidth();
        y1 = bounds.getY();
        y2 = bounds.getY() + bounds.getHeight();

        double m, b;
        m = (line.getY2() - line.getY1()) / (line.getX2() - line.getX1());
        b = ((m * (0 - line.getX1())) + line.getY1());

        double YAtX1, YAtX2, XAtY1, XAtY2;

        YAtX1 = m * x1 + b;
        YAtX2 = m * x2 + b;
        XAtY1 = (y1 - b) / m;
        XAtY2 = (y2 - b) / m;

        ArrayList<Point2D> points = new ArrayList<Point2D>();
        Point2D p = null;
        if (isBetween(YAtX1, y1, y2)) {
            p = new Point2D.Double(x1, YAtX1);
            points.add(p);
        }
        if (isBetween(YAtX2, y1, y2)) {
            p = new Point2D.Double(x2, YAtX2);
            points.add(p);
        }
        if (isBetween(XAtY1, x1, x2)) {
            p = new Point2D.Double(XAtY1, y1);
            points.add(p);
        }
        if (isBetween(XAtY2, x1, x2)) {
            p = new Point2D.Double(XAtY2, y2);
            points.add(p);
        }
        if (points.size() == 0) {
            return null;
        }
        if (points.size() == 1) {
            return p;
        } else {
            return findShortLine(points, line.getP1());
        }
    }

    private boolean isBetween(double num, double min, double max) {
        return num >= min && num <= max;
    }

    private Point2D findShortLine(ArrayList<Point2D> points, Point2D center) {
        Point2D p = null;
        double shortestLength = range * Tile.WIDTH;
        for (Point2D point2D : points) {
            Line2D newLine = new Line2D.Double(center, point2D);
            double xDist = (x * Tile.WIDTH) - newLine.getX2();
            double yDist = (y * Tile.HEIGHT) - newLine.getY2();
            double length = (Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2)));
            if (length < shortestLength) {
                shortestLength = length;
                p = point2D;
            }
        }
        return p;
    }


    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) ((x - (range / 2)) * Tile.WIDTH), (int) ((y - (range / 2)) * Tile.HEIGHT), (int) range * Tile.WIDTH, (int) range * Tile.WIDTH);
    }
}
