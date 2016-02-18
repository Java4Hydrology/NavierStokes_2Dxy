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

import java.util.ArrayList;

import org.geotools.graph.util.geom.Coordinate2D;

import it.blogspot.geoframe.key.Key;

public class Polygon {

    final int numberOfSides;
    final ArrayList<Key> sides;
    final double area;
    final Coordinate2D barycenter;

    public Polygon(final Coordinate2D barycenter, final double area, final ArrayList<Key> sides) {

        this.barycenter = barycenter;
        this.area = area;
        this.sides = sides;
        this.numberOfSides = sides.size();

    }

    @Override
    public String toString() {

        String message = "-----------------------------------------------\n";
        message += "Number of sides: " + numberOfSides + "\n";
        message += "Area: " + area + "\n";
        message += "Barycenter: " + barycenter + "\n";
        message += "Sides keys: \n";

        for (Key side : sides)
            message += "\t" + side.getInteger() + "\n";

        return message;

    }

    public String toString(final Key key) {

        String message = "POLYGON " + key.getInteger() + " ------------------------\n";
        message += "Number of sides: " + numberOfSides + "\n";
        message += "Area: " + area + "\n";
        message += "Barycenter: " + barycenter + "\n";
        message += "Sides keys: \n";

        for (Key side : sides)
            message += "\t" + side.getInteger() + "\n";

        return message;

    }

}
