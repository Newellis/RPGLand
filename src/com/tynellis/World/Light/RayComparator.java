package com.tynellis.World.Light;

import java.awt.geom.Line2D;
import java.util.Comparator;

public class RayComparator implements Comparator<Line2D> {

    @Override
    public int compare(Line2D o1, Line2D o2) {
        double line1Angle = Math.atan(o1.getY2() - o1.getY1()) / (o1.getX2() - o1.getX1());
        double line2Angle = Math.atan(o2.getY2() - o2.getY1()) / (o2.getX2() - o2.getX1());
        if (line1Angle < 0) {
            line1Angle += Math.PI;
        }
        if (line2Angle < 0) {
            line2Angle += Math.PI;
        }
        return Double.compare(line1Angle, line2Angle);
    }
}
