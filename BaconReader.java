import java.util.*;
import java.io.*;


/**
 * A BaconReader object is meant to read files formatted to provide data for the Kevin Bacon game.
 * Formatting: In any file, each line contains two pieces of information separated by a "|"
 *     actors: actor ID| actor name
 *     movie-actors: movie ID| actor ID
 *     movies: movie ID| movie name
 *
 * @author Connor Hay, Dartmouth CS 10, Spring 2021
 */
public class BaconReader {
    /**
     * Read from an actor or movie file and return the enclosed data as a map.
     *
     * @param fileName the file name or its path to open and read
     * @return a map containing the actor or movie IDs and the corresponding names
     */
    public static Map<Integer, String> readIDs(String fileName) {
        Map<Integer, String> IDMap = new HashMap<>(); // to hold all the names and their respective IDs
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));

            String currentLine;
            while ((currentLine = input.readLine()) != null) { // read every line of the file
                String[] lineContents = currentLine.split("\\|");  // split the line at the |, store the parts in an array
                String ID = lineContents[0];
                String Name = lineContents[1];
                IDMap.put(Integer.parseInt(ID), Name);
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IDMap;
    }

    /**
     * Read from a movie-actor file and return the enclosed data as a map.
     *
     * @param fileName the file name or its path to open and read
     * @return a map containing the the movie IDs and the corresponding list of actor IDs
     */
    public static Map<Integer, ArrayList<Integer>> readMovieActors(String fileName) {
        Map<Integer, ArrayList<Integer>> movieActors = new HashMap<>(); // to hold all the movies and their respective actors
        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));

            String currentLine;
            while ((currentLine = input.readLine()) != null) { // read every line of the file
                String[] lineContents = currentLine.split("\\|"); // split the line at the |, store the parts in an array
                Integer movieID = Integer.parseInt(lineContents[0]);
                Integer actorID = Integer.parseInt(lineContents[1]);
                ArrayList<Integer> actorList;

                // check if the movie already has a list (i.e. exists in the map)
                if (!movieActors.containsKey(movieID)) {
                    actorList = new ArrayList<>(); // create the list from scratch
                }
                else {
                    actorList = movieActors.get(movieID); // get the existing actor list
                    movieActors.remove(movieID);
                }
                actorList.add(actorID);
                movieActors.put(movieID, actorList);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieActors;
    }
}
