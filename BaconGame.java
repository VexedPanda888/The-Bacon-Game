import java.util.*;

/**
 * Central program for problem set 4
 * This problem set tackles the important social network problem of finding an actor's "Bacon number".
 * Starting with an actor, see if they have been in a movie with someone who has been in a movie with someone who has been in a movie ... who has been in a movie with Kevin Bacon.
 * They're usually at most 6 steps away.
 *
 * @author Connor Hay, Dartmouth CS 10, Spring 2021
 */
public class BaconGame {
    Graph<String, Set<String>> universe;
    String center;
    Graph<String, Set<String>> BFSTree;
    Map<String, Double> averageSeparationMap;

    /**
     * Start the Bacon Game!
     * Allows manual change of files containing actor and movie data.
     */
    public static void main(String[] args) {
        String actors = "bacon/actorsTest.txt";
        String movies = "bacon/moviesTest.txt";
        String movieActors = "bacon/movie-actorsTest.txt";
        BaconGame running = new BaconGame(actors, movies, movieActors);
        running.start();
    }

    /**
     * Constructor completely loads data from files and constructs the universe graph.
     * It initializes the universe with Kevin Bacon at the center, populating all instance variables with appropriate values.
     *
     * @param actorsFileName file with actors and their ID numbers
     * @param moviesFileName file with movies and their ID numbers
     * @param movieActorsFileName file with movies and their actors, both as ID number
     */
    public BaconGame(String actorsFileName, String moviesFileName, String movieActorsFileName) {
        System.out.println("Loading universe... please wait. This may take a minute or two.");
        universe = new AdjacencyMapGraph<>(); // create the universe
        // read in movie and actor data
        Map<Integer, String> actorMap = BaconReader.readIDs(actorsFileName);
        Map<Integer, String> movieMap = BaconReader.readIDs(moviesFileName);
        Map<Integer, ArrayList<Integer>> movieActorsMap = BaconReader.readMovieActors(movieActorsFileName);
        // insert actor names for vertices
        for(int ID: actorMap.keySet()) universe.insertVertex(actorMap.get(ID));
        // insert movie name sets for edges
        for (int movieID: movieMap.keySet()) { // loop over movie names
            if (movieActorsMap.containsKey(movieID)) {
                for (int actorID : movieActorsMap.get(movieID)) { // loop over movie's actor list
                    for (int otherActorID : movieActorsMap.get(movieID)) { // loop again for the other actors
                        if (actorID != otherActorID) { // exclude self-loops
                            String actor = actorMap.get(actorID);
                            String otherActor = actorMap.get(otherActorID);
                            String movie = movieMap.get(movieID);
                            if (universe.hasEdge(actor, otherActor)) { // add to existing edge set
                                Set<String> edgeSet = universe.getLabel(actor, otherActor);
                                edgeSet.add(movie);
                                universe.removeDirected(actor, otherActor);
                                universe.insertDirected(actor, otherActor, edgeSet);
                            } else { // create new edge set
                                Set<String> edgeSet = new HashSet<>();
                                edgeSet.add(movie);
                                universe.insertDirected(actor, otherActor, edgeSet);
                            }
                        }
                    }
                }
            }
        }
        center = "Kevin Bacon"; // initialize with Kevin as the center
        BFSTree = graphLibrary.bfs(universe, center); // Kevin's BFSTree
        // calculate all average separations in this universe
        // front-loaded these calculations because a map of strings and doubles is not especially memory-demanding
        averageSeparationMap = new HashMap<>();
        for (String actor: universe.vertices()){
            Graph<String, Set<String>> actorTree = graphLibrary.bfs(universe, actor);
            double actorSeparation = graphLibrary.averageSeparation(actorTree, actor);
            averageSeparationMap.put(actor, actorSeparation);
        }
    }

    /**
     * A brief helper method mostly implemented for easier comprehension of this code
     */
    private void start() {
        System.out.println(
                """
                Commands:
                c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
                d <low> <high>: list actors sorted by degree, with degree between low and high
                i: list actors with infinite separation from the current center
                p <name>: find path from <name> to current center of the universe
                s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high
                u <name>: make <name> the center of the universe
                q: quit game
                """
        );
        mainMenu();
    }

    /**
     * Primary user interface of the game. Takes a single character choice, then prompts for more information before
     * passing choices to a helper method.
     */
    private void mainMenu() {
        Scanner inputCommands = new Scanner(System.in);
        System.out.println(center+" is now the center of the acting universe, connected to "+BFSTree.numVertices()+" of "+universe.numVertices()+" actors with average separation "+averageSeparationMap.get(center));
        System.out.print("Enter a letter >>");
        String choice = inputCommands.nextLine().substring(0,1); // only accept the first character entered

        switch (choice) {
            case "q" -> Runtime.getRuntime().exit(1);
            case "c" -> {
                int max = universe.numVertices();
                int number;
                do {
                    System.out.print("Enter an integer n, such that 1<=|n|<=" + max + " >>");
                    number = inputCommands.nextInt();
                } while (Math.abs(number) > max || number == 0);
                printCentersOfTheUniverse(number);
            }
            case "d" -> {
                System.out.print("Enter an integer for the lowest allowed degree >>");
                int lowD = inputCommands.nextInt();
                System.out.print("Enter an integer for the highest allowed degree >>");
                int highD = inputCommands.nextInt();
                printActorsByDegree(lowD, highD);
            }
            case "i" -> printInfiniteSeparation();
            case "p" -> {
                String actorName;
                do {
                    System.out.print("Enter a connected actor name in the universe >>");
                    actorName = inputCommands.nextLine();
                } while (actorName.equals(center) || !BFSTree.hasVertex(actorName));
                printPathToCenter(actorName);
            }
            case "s" -> {
                System.out.print("Enter an integer for the lowest allowed separation >>");
                int lowS = inputCommands.nextInt();
                System.out.print("Enter an integer for the highest allowed separation >>");
                int highS = inputCommands.nextInt();
                printActorsBySeparation(lowS, highS);
            }
            case "u" -> {
                String newCenter;
                do {
                    System.out.print("Enter an actor name in the universe >>");
                    newCenter = inputCommands.nextLine();
                } while (!universe.hasVertex(newCenter));
                changeCenter(newCenter);
            }
            default -> System.out.println("ERROR: Invalid character, try again");
        }
        proceed();
    }

    /**
     * Helper method to create, sort, and display a restricted list of actors based on separation from the center.
     *
     * @param low lower bound of separation in the list
     * @param high higher bound of separation in the list
     */
    private void printActorsBySeparation(int low, int high) {
        Map<String, Integer> separationMap = new HashMap<>();
        for (String actor: BFSTree.vertices()) {
            int separation = graphLibrary.getPath(BFSTree, actor).size()-1;
            if (separation >= low && separation <= high) separationMap.put(actor, separation);
        }
        ArrayList<String> actorList = new ArrayList<>(separationMap.keySet());
        actorList.sort(Comparator.comparingInt(separationMap::get));
        System.out.println(actorList);
    }

    /**
     * Helper method that prints the path from the given actor to the center actor
     *
     * @param actorName name of actor in the universe connected to the center
     */
    private void printPathToCenter(String actorName) {
        List<String> path = graphLibrary.getPath(BFSTree, actorName);
        System.out.println(path);
    }

    /**
     * Helper method to find and display the universe's disconnected actors
     */
    private void printInfiniteSeparation() {
        System.out.println(graphLibrary.missingVertices(universe, BFSTree));
    }

    /**
     * Helper method to change the center of the universe and update dependent instance variables
     *
     * @param actorName name of an actor in the universe
     */
    private void changeCenter(String actorName) {
        center = actorName; // change center
        BFSTree = graphLibrary.bfs(universe, center); // New BFSTree
        System.out.println(center + " is the new center of the universe!");
    }

    /**
     * Helper method to create, sort, and display a restricted list of actors based on degree.
     *
     * @param low lower bound of degree in the list
     * @param high higher bound of degree in the list
     */
    private void printActorsByDegree(int low, int high) {
        ArrayList<String> actorList = new ArrayList<>();
        for (String actor: universe.vertices()) {
            int degree = universe.inDegree(actor);
            if (degree >= low && degree <= high) {
                actorList.add(actor);
            }
        }
        actorList.sort(Comparator.comparingInt(actor -> universe.inDegree(actor)));
        System.out.println(actorList);
    }

    /**
     * Helper method to facilitate returning to the main menu and making another selection
     */
    private void proceed() {
        System.out.println("\nPress enter to return make another selection from the main menu >>");
        Scanner check = new Scanner(System.in);
        check.nextLine(); // wait for a line to be entered
        mainMenu(); // return to the main menu
    }

    /**
     * Helper method to find and print the best or worst centers of the universe
     *
     * @param number of actors to show. positive: top. negative: bottom.
     */
    private void printCentersOfTheUniverse(int number) {
        ArrayList<String> result = new ArrayList<>();
        for (String actor: BFSTree.vertices()) result.add(actor); // populate the unordered list
        // sort using the pre-calculated averageSeparation values of each actor
        result.sort(Comparator.comparingDouble(actor -> averageSeparationMap.get(actor)));

        //print
        System.out.print("[");
        if (number > 0) {
            // print best to worse
            for(int i = 0; i < number; i++) {
                System.out.print(result.get(i) + ": " + averageSeparationMap.get(result.get(i)));
                if (i != number - 1) System.out.print(", ");
            }
        }
        else {
            // print worst to better
            for(int i = result.size() - 1; i >= result.size() + number; i--) {
                System.out.print(result.get(i) + ": " + averageSeparationMap.get(result.get(i)));
                if (i != number - 1) System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}
