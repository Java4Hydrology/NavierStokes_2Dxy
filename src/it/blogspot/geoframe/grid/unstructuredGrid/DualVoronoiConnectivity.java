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


public class DualVoronoiConnectivity {

    int[][] delaunayTriangulation;
    int[][] trianglesAttachedToNode; // dual
    public int[][] neighbor; // Neighbor
    public int[][] neighborEdges; // NeighborSide
    int[][] localNodesIndeces = {{0,1},{1,2},{2,0}};
    int[] numberOfTrianglesAttachedToNode; // ndual
    int numberOfPolygons;

    DualVoronoiConnectivity (final int[][] delaunayTriangulation) {

        this.delaunayTriangulation = delaunayTriangulation;
        int numberOfNodes = getNumberOfNodes();
        trianglesAttachedToNode = new int[numberOfNodes][6];
        numberOfTrianglesAttachedToNode = new int[numberOfNodes];
        numberOfPolygons = computeNumberOfPolygons();
        neighbor = new int[numberOfPolygons][3];
        neighborEdges = new int[numberOfPolygons][3];

        build();
        computeNeighbors();

    }

    private int computeNumberOfPolygons() {
        return delaunayTriangulation.length;
    }

    private int getNumberOfNodes() {

        int max = delaunayTriangulation[0][0];

        for (int row = 0; row < delaunayTriangulation.length; row++) {
            for(int column = 0; column < delaunayTriangulation[0].length; column++) {
                if (max < delaunayTriangulation[row][column])
                    max = delaunayTriangulation[row][column];
            }
        }

        return max;

    }

    private void build() {

        for (int polygon = 0; polygon < numberOfPolygons; polygon++) {
            nodeProcessing(polygon);
        }

    }

    private void nodeProcessing(final int polygon) {

        int tmpNode;

        for (int localNodeIndex = 0; localNodeIndex < delaunayTriangulation[0].length; localNodeIndex++) {

            tmpNode = delaunayTriangulation[polygon][localNodeIndex];
            numberOfTrianglesAttachedToNode[tmpNode - 1] =
                numberOfTrianglesAttachedToNode[tmpNode - 1] + 1;
            trianglesAttachedToNode[tmpNode - 1][numberOfTrianglesAttachedToNode[tmpNode - 1] - 1] =
                polygon + 1;

        }
    }

    private void computeNeighbors() {

        for (int polygon = 0; polygon < numberOfPolygons; polygon++)
            edgesProcessing(polygon);

    }

    private void edgesProcessing(final int polygon) {

        for (int edge = 0; edge < 3; edge++) {
            int firstNode = delaunayTriangulation[polygon][localNodesIndeces[edge][0]];
            int secondNode = delaunayTriangulation[polygon][localNodesIndeces[edge][1]];

            neighborProcessing(polygon, edge, firstNode, secondNode);

        }

    }

    private void neighborProcessing(final int polygon, final int edge, final int firstNode, final int secondNode) {

        for (int neighborCounter = 0; neighborCounter < numberOfTrianglesAttachedToNode[firstNode - 1]; neighborCounter++) {
            int tmpNeighbor = trianglesAttachedToNode[firstNode - 1][neighborCounter];

            if (polygon + 1 == tmpNeighbor) continue;

            neighborEdgesProcessing(polygon, edge, tmpNeighbor, firstNode, secondNode);

            if (neighbor[polygon][edge] > 0) break;

        }

    }

    private void neighborEdgesProcessing(final int polygon, final int edge, final int tmpNeighbor, final int firstNode, final int secondNode) {

        for (int neighborEdgeCounter = 0; neighborEdgeCounter < 3; neighborEdgeCounter++) {
            int neighborEdgeFirstNode = delaunayTriangulation[tmpNeighbor - 1][localNodesIndeces[neighborEdgeCounter][0]];
            int neighborEdgeSecondNode = delaunayTriangulation[tmpNeighbor - 1][localNodesIndeces[neighborEdgeCounter][1]];

            if (firstNode == neighborEdgeSecondNode &&
                secondNode == neighborEdgeFirstNode ||
                firstNode == neighborEdgeFirstNode &&
                secondNode == neighborEdgeSecondNode) {
                neighbor[polygon][edge] = tmpNeighbor;
                neighborEdges[polygon][edge] = neighborEdgeCounter + 1;
                break;
            }

        }

    }

    public int getNumberOfPolygons() {
        return numberOfPolygons;
    }

    public int getNeighborPolygon(final int polygon, final int edge) {
        return neighbor[polygon][edge];
    }

    public int getIndexOfLocalNode(final int polygon, final int edge, final int localNodeIndex)
    {
        int index = localNodesIndeces[edge][localNodeIndex];
        return delaunayTriangulation[polygon][index];
    }

    public static void main(String[] args) {

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

        DualVoronoiConnectivity dualVoronoiConnectivity = new DualVoronoiConnectivity(tri);

        for (int i = 0; i < dualVoronoiConnectivity.neighborEdges.length; i++) {
            for (int j = 0; j < dualVoronoiConnectivity.neighborEdges[0].length; j++) {
                System.out.print(dualVoronoiConnectivity.neighborEdges[i][j] + " ");
            }
            System.out.print("\n");
        }

    }

}
