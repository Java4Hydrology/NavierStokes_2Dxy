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
package it.blogspot.geoframe.grid.unstructuredGrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.geotools.graph.util.geom.Coordinate2D;

import it.blogspot.geoframe.grid.Edge;
import it.blogspot.geoframe.grid.Polygon;
import it.blogspot.geoframe.key.Key;
import it.blogspot.geoframe.utils.GeometryUtils;

public class DelaunayGrid {

    ConcurrentHashMap<Key, Polygon> polygons;
    ConcurrentHashMap<Key, Edge> edges;
    int numberOfPolygons = 0;
    int numberOfEdges = 0;

    public DelaunayGrid(final int[][] delaunayTriangulation,
                        final Coordinate2D[] nodesCoordinates)
    {

        DualVoronoiConnectivity dualVoronoiConnectivity = new
            DualVoronoiConnectivity(delaunayTriangulation);
        this.numberOfPolygons = dualVoronoiConnectivity.getNumberOfPolygons();

        polygons = new ConcurrentHashMap<Key, Polygon>();
        edges = new ConcurrentHashMap<Key, Edge>();

        buildGrid(dualVoronoiConnectivity, nodesCoordinates);

    }

    private void buildGrid(final DualVoronoiConnectivity dualVoronoiConnectivity,
                           final Coordinate2D[] nodesCoordinates)
    {

        for (int polygonIndex = 0; polygonIndex < numberOfPolygons; polygonIndex++) {
            Key tmpKey = new Key(polygonIndex + 1);
            Polygon tmpPolygon = buildPolygon(polygonIndex,
                    dualVoronoiConnectivity, nodesCoordinates);
            polygons.put(tmpKey, tmpPolygon);
        }

    }

    private Polygon buildPolygon(final int polygonIndex,
                                 final DualVoronoiConnectivity dualVoronoiConnectivity,
                                 final Coordinate2D[] nodesCoordinates)
    {

        final Coordinate2D[] polygonNodesCoordinates =
            computeVertecesCoordinates(polygonIndex,
                                       dualVoronoiConnectivity,
                                       nodesCoordinates);

        double[][] jacobian = GeometryUtils.jacobianMapping(polygonNodesCoordinates);
        double area = GeometryUtils.computePolygonArea(jacobian);
        Coordinate2D circumcenter =
            GeometryUtils.computePolygonCircumcenter(jacobian,
                    polygonNodesCoordinates[0]);

        ArrayList<Key> polygonEdges = computePolygonEdges(polygonIndex,
                dualVoronoiConnectivity, nodesCoordinates, circumcenter);

        return new Polygon(circumcenter, area, polygonEdges);

    }

    private Coordinate2D[] computeVertecesCoordinates(final int polygonIndex,
                                                      final DualVoronoiConnectivity dualVoronoiConnectivity,
                                                      final Coordinate2D[] nodesCoordinates) {

        Coordinate2D[] tmpArrayOfCoordinates = new Coordinate2D[3];

        tmpArrayOfCoordinates[0] =
            nodesCoordinates[dualVoronoiConnectivity.delaunayTriangulation[polygonIndex][0] - 1];

        tmpArrayOfCoordinates[1] =
            nodesCoordinates[dualVoronoiConnectivity.delaunayTriangulation[polygonIndex][1] - 1];

        tmpArrayOfCoordinates[2] =
            nodesCoordinates[dualVoronoiConnectivity.delaunayTriangulation[polygonIndex][2] - 1];

        return tmpArrayOfCoordinates;

    }

    private ArrayList<Key> computePolygonEdges(final int polygonIndex,
                                               final DualVoronoiConnectivity dualVoronoiConnectivity,
                                               final Coordinate2D[] nodesCoordinates,
                                               final Coordinate2D circumcenter)
    {

        ArrayList<Key> tmpKeys = new ArrayList<Key>();

        for (int sideIndex = 0; sideIndex < 3; sideIndex++) {
            int neighborIndex = dualVoronoiConnectivity.getNeighborPolygon(polygonIndex,
                                                                           sideIndex);

            Key edgeKey = Key.modifiedCantorPairing(polygonIndex + 1, neighborIndex);
            int tmpVarToDelete = polygonIndex + 1;
            System.out.println("Key: " + edgeKey.getDouble() + ", Polygon Index:" + tmpVarToDelete + ", Neighbor Index: " + neighborIndex);
            if(edges.get(edgeKey) == null) {
                Edge tmpEdge = buildEdge(polygonIndex, neighborIndex,
                                         sideIndex, nodesCoordinates,
                                         dualVoronoiConnectivity);

                edges.put(edgeKey, tmpEdge);
            }
            tmpKeys.add(edgeKey);
        }

        return tmpKeys;

    }

    private Edge buildEdge(final int polygonIndex, final int neighborIndex,
                           final int sideIndex, final Coordinate2D[] nodesCoordinates,
                           final DualVoronoiConnectivity dualVoronoiConnectivity)
    {

        numberOfEdges += 1;
        int node1 =
            dualVoronoiConnectivity.getIndexOfLocalNode(polygonIndex, sideIndex, 0);

        int node2 =
            dualVoronoiConnectivity.getIndexOfLocalNode(polygonIndex, sideIndex, 1);

        Coordinate2D[] verteces = {nodesCoordinates[node1 - 1], nodesCoordinates[node2 - 1]};

        double length = GeometryUtils.computeLength(verteces[0], verteces[1]);
        Coordinate2D barycenter = GeometryUtils.computeLineBarycenter(verteces[0], verteces[1]);
        Key left = new Key(polygonIndex + 1);
        Key right = new Key(neighborIndex);

        return new Edge(barycenter, length, left, right, verteces);

    }

    public static void main (String[] args) {

        int[][] tri = {{2,5,1},
                       {4,5,1},
                       {4,5,8},
                       {6,2,3},
                       {6,2,5},
                       {7,6,10},
                       {7,6,3},
                       {9,5,8},
                       {9,6,5},
                       {9,6,10}};

        Coordinate2D[] nodesCoordinates = {new Coordinate2D(-1, -1),
                                           new Coordinate2D(0,-1),
                                           new Coordinate2D(1,-1),
                                           new Coordinate2D(-1,0),
                                           new Coordinate2D(-0.5,0),
                                           new Coordinate2D(0.5,0),
                                           new Coordinate2D(1,0),
                                           new Coordinate2D(-1,1),
                                           new Coordinate2D(0,1),
                                           new Coordinate2D(1,1)};

        DelaunayGrid delaunayGrid = new DelaunayGrid(tri, nodesCoordinates);

        Iterator iterator = delaunayGrid.polygons.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            Polygon polygon = (Polygon)pair.getValue();
            Key key = (Key)pair.getKey();
            System.out.println(polygon.toString(key));
        }

        System.out.println("Number of edges: " + delaunayGrid.numberOfEdges);

        iterator = delaunayGrid.edges.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            Edge edge = (Edge)pair.getValue();
            Key key = (Key)pair.getKey();
            // System.out.println(edge.toString(key));
        }

    }

}
