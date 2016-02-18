/*
 * GNU GPL v3 License
 *
 * Copyright 2015 AboutHydrology (Riccardo Rigon)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.blogspot.geoframe.utils;

import org.geotools.graph.util.geom.Coordinate2D;

public class GeometryUtils {

    public static double computeLength(final Coordinate2D nodeA, final Coordinate2D nodeB) {

        double xSquared = Math.pow(nodeA.x - nodeB.x, 2);
        double ySquared = Math.pow(nodeA.y - nodeB.y, 2);

        return Math.sqrt(xSquared + ySquared);

    }

    public static Coordinate2D computeLineBarycenter(final Coordinate2D nodeA, final Coordinate2D nodeB) {

        double x = 0.5 * (nodeA.x + nodeB.x);
        double y = 0.5 * (nodeA.y + nodeB.y);

        return new Coordinate2D(x, y);

    }

    public static double computePolygonArea(final double[][] jacobian) {
        return 0.5*(jacobian[0][0] * jacobian[1][1] - jacobian[0][1] * jacobian[1][0]);
    }

    public static Coordinate2D computePolygonCircumcenter(final double[][] jacobian, final Coordinate2D node) {

        // from Wiki
        double[] a = {jacobian[0][0], jacobian[1][0]};
        double[] b = {jacobian[0][1], jacobian[1][1]};
        double a2 = a[0] * a[0] + a[1] * a[1];
        double b2 = b[0] * b[0] + b[1] * b[1];
        double[] c = {a2 * b[0] - b2 * a[0], a2 * b[1] - b2 * a[1]};

        double cb = c[0] * b[0] + c[1] * b[1];
        double ca = c[0] * a[0] + c[1] * a[1];
        double ab = a[0] * b[0] + a[1] * b[1];

        double x = node.x + 0.5 * (cb * a[0] - ca * b[0]) / (a2 * b2 - ab * ab);
        double y = node.y + 0.5 * (cb * a[1] - ca * b[1]) / (a2 * b2 - ab * ab);

        return new Coordinate2D(x, y);

    }

    public static double[][] jacobianMapping(final Coordinate2D[] polygonNodesCoordinates) {

        double[][] jacobian = new double[2][2];

        jacobian[0][0] = polygonNodesCoordinates[2].x - polygonNodesCoordinates[1].x;
        jacobian[0][1] = polygonNodesCoordinates[3].x - polygonNodesCoordinates[1].x;
        jacobian[1][0] = polygonNodesCoordinates[2].y - polygonNodesCoordinates[1].y;
        jacobian[1][1] = polygonNodesCoordinates[3].y - polygonNodesCoordinates[1].y;

        return jacobian;

    }

}
