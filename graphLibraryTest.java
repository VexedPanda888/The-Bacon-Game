import java.util.HashSet;
import java.util.Set;

/**
 * Test class for graphLibrary.java
 * Expected output commented below
 *
 * @author Connor Hay, Dartmouth CS 10, Spring 2021
 */
public class graphLibraryTest {
    public static void main(String[] args) {
        Graph<String, Set<String>> testGraph = new AdjacencyMapGraph<>();

        // setup testGraph
        testGraph.insertVertex("Kevin Bacon");
        testGraph.insertVertex("Alice");
        testGraph.insertVertex("Bob");
        testGraph.insertVertex("Charlie");
        testGraph.insertVertex("Dartmouth");
        testGraph.insertVertex("Nobody");
        testGraph.insertVertex("Nobody's Friend");
        Set<String> currentEdge = new HashSet<>();
        currentEdge.add("A Movie");
        currentEdge.add("E Movie");
        testGraph.insertUndirected("Kevin Bacon", "Alice", currentEdge);
        currentEdge = new HashSet<>();
        currentEdge.add("A Movie");
        testGraph.insertUndirected("Kevin Bacon", "Bob", currentEdge);
        currentEdge = new HashSet<>();
        currentEdge.add("A Movie");
        testGraph.insertUndirected("Alice", "Bob", currentEdge);
        currentEdge = new HashSet<>();
        currentEdge.add("D Movie");
        testGraph.insertUndirected("Alice", "Charlie", currentEdge);
        currentEdge = new HashSet<>();
        currentEdge.add("C Movie");
        testGraph.insertUndirected("Bob", "Charlie", currentEdge);
        currentEdge = new HashSet<>();
        currentEdge.add("B Movie");
        testGraph.insertUndirected("Charlie", "Dartmouth", currentEdge);
        currentEdge = new HashSet<>();
        currentEdge.add("F Movie");
        testGraph.insertUndirected("Nobody", "Nobody's Friend", currentEdge);

        // graph setup check
        System.out.println("The graph:");
        System.out.println(testGraph);

        System.out.println("bfs check");
        Graph<String, Set<String>> bfsTree = graphLibrary.bfs(testGraph, "Kevin Bacon");
        System.out.println(bfsTree);

        System.out.println("getPath check");
        System.out.println(graphLibrary.getPath(bfsTree, "Alice"));
        System.out.println(graphLibrary.getPath(bfsTree, "Bob"));
        System.out.println(graphLibrary.getPath(bfsTree, "Charlie"));
        System.out.println(graphLibrary.getPath(bfsTree, "Dartmouth"));
        System.out.println(graphLibrary.getPath(bfsTree, "Nobody"));
        System.out.println(graphLibrary.getPath(bfsTree, "Nobody's Friend"));

        System.out.println("missingVertices check");
        System.out.println(graphLibrary.missingVertices(testGraph, bfsTree));

        System.out.println("averageSeparation check");
        System.out.println(graphLibrary.averageSeparation(bfsTree, "Kevin Bacon"));
    }

    /* OUTPUT
    "C:\Program Files\Java\jdk-16\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2020.3.3\lib\idea_rt.jar=56786:C:\Program Files\JetBrains\IntelliJ IDEA 2020.3.3\bin" -Dfile.encoding=UTF-8 -classpath C:\Users\cwhay\Documents\cs10\libs\opencv.jar;C:\Users\cwhay\Documents\cs10\libs\openblas-windows-x86_64.jar;C:\Users\cwhay\Documents\cs10\libs\javacv.jar;C:\Users\cwhay\Documents\cs10\libs\opencv-windows-x86_64.jar;C:\Users\cwhay\Documents\cs10\libs\json-simple-1.1.1.jar;C:\Users\cwhay\Documents\cs10\libs\net-datastructures-4-0.jar;C:\Users\cwhay\Documents\cs10\out\production\cs10 graphLibraryTest
    The graph:
    Vertices: [Bob, Dartmouth, Alice, Charlie, Nobody, Nobody's Friend, Kevin Bacon]
    Out edges: {Bob={Alice=[A Movie], Charlie=[C Movie], Kevin Bacon=[A Movie]}, Dartmouth={Charlie=[B Movie]}, Alice={Bob=[A Movie], Charlie=[D Movie], Kevin Bacon=[A Movie, E Movie]}, Charlie={Bob=[C Movie], Dartmouth=[B Movie], Alice=[D Movie]}, Nobody={Nobody's Friend=[F Movie]}, Nobody's Friend={Nobody=[F Movie]}, Kevin Bacon={Bob=[A Movie], Alice=[A Movie, E Movie]}}
    bfs check
    Vertices: [Bob, Dartmouth, Alice, Charlie, Kevin Bacon]
    Out edges: {Bob={Kevin Bacon=null}, Dartmouth={Charlie=null}, Alice={Kevin Bacon=null}, Charlie={Bob=null}, Kevin Bacon={}}
    getPath check
    [Kevin Bacon, Alice]
    [Kevin Bacon, Bob]
    [Kevin Bacon, Bob, Charlie]
    [Kevin Bacon, Bob, Charlie, Dartmouth]
    Vertex Nobody not found in tree.
    null
    Vertex Nobody's Friend not found in tree.
    null
    missingVertices check
    [Nobody, Nobody's Friend]
    averageSeparation check
    1.75

    Process finished with exit code 0
     */
}
