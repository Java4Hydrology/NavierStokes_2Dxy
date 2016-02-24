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
package it.blogspot.geoframe.grid;

import org.geotools.graph.util.geom.Coordinate2D;

import it.blogspot.geoframe.key.Key;

public class Edge {

    Coordinate2D barycenter;
    double length;
    double distanceBetweenCentersAdjacentPolygons;
    Key left;
    Key right;
    Coordinate2D[] edgeVerteces;

    public Edge(final Coordinate2D barycenter, final double length, final Key left, final Key right, final Coordinate2D[] edgeVerteces) {

        this.barycenter = barycenter;
        this.length = length;
        this.left = left;
        this.right = right;
        this.edgeVerteces = edgeVerteces;

    }

}
