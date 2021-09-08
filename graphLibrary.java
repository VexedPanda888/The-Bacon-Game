import java.util.*;
import java.io.*;

/**
 * graphLibrary as requested in problem set 4
 *
 * @author Connor Hay, Dartmouth CS 10, Spring 2021
 * @author Tim Pierson, Dartmouth CS 10, Spring 2019
 */

public class graphLibrary {
    /**
     * Mirrors BFS method of GraphTraversal.java but implements backTrack with a Graph (path tree) instead of a HashMap.
     *
     * @param g graph to search
     * @param source starting vertex
     * @return a graph containing the BFS tree named backTrack
     */
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) {
        Graph<V, E> backTrack = new AdjacencyMapGraph<>(); //initialize backTrack as a graph
        backTrack.insertVertex(source);
        Set<V> visited = new HashSet<V>();
        Queue<V> queue = new LinkedList<V>();

        queue.add(source);
        visited.add(source);
        while (!queue.isEmpty()) {
            V u = queue.remove();
            for (V v : g.outNeighbors(u)) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                    backTrack.insertVertex(v);
                    backTrack.insertDirected(v, u, null); // save discovery edge direct backwards
                }
            }
        }

        return backTrack;
    }

    /**
     * Given a BFS tree and vertex, return the path from the vertex to the root
     *
     * @param tree BFS tree containing paths directed toward a root (center)
     * @param v end vertex of the final path of interest
     * @return the path from the root to v as a list of vertices
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        if (!tree.hasVertex(v)) {
            System.out.println("Vertex "+v+" not found in tree.");
            return null;
        }
        // construct path working back from vertex v
        else {
            LinkedList<V> path = new LinkedList<>(); // linked list preferred because of operations at index 0
            V current =  v;
            path.add(0, current);
            // current will be null if at root because there are no out-neighbors
            while (tree.outDegree(current) != 0) {
                for (V vertex: tree.outNeighbors(current)) current = vertex;
                path.add(0, current);
            }
            return path;
        }
    }

    /**
     * Given a graph and subgraph, return the set of vertices in the graph but not the subgraph
     *
     * @param graph graph to compare
     * @param subgraph subgraph to compare
     * @return a set of the vertices in graph but not in subgraph
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
        Set<V> result = new HashSet<>();
        // add all vertices in graph
        for (V vertex: graph.vertices()) {
            result.add(vertex);
        }
        // remove all vertices in subgraph
        for (V vertex: subgraph.vertices()) {
            result.remove(vertex);
        }
        return result;
    }

    /**
     * Find the average distance-from-root in a BFS tree
     *
     * @param tree BFS tree containing paths directed toward root
     * @param root initially passed the root of the BFS tree, but is just a vertex in recursive calls
     * @return the average distance-from-root (separation) in tree
     */
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
        double total = averageSeparationHelper(tree, root, 1);
        int n = tree.numVertices()-1;
        return total/n;
    }

    private static <V,E> int averageSeparationHelper(Graph<V,E> tree, V root, int level) {
        int total = 0;
        for (V vertex: tree.inNeighbors(root)) {
            total += level;
            total += averageSeparationHelper(tree, vertex, level + 1);
        }
        return total;
    }
}
