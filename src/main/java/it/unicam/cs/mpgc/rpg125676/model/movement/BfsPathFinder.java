package it.unicam.cs.mpgc.rpg125676.model.movement;

import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * Finds the shortest path between two rooms using Breadth-First Search.
 * The house is a graph in which rooms are nodes and room connections are edges.
 * Breadth-First Search guarantees the shortest path in terms of number of room transitions.
 */
public class BfsPathFinder implements PathFinder {

    /**
     * Finds the shortest path between the specified starting room
     * and destination.
     * If the starting room and destination are the same, the returned
     * path contains only that room. If the destination cannot be reached,
     * an empty list is returned.
     *
     * @param house house containing the rooms and their connections
     * @param start starting room of the search
     * @param destination destination room to reach
     * @return the shortest ordered path including start and destination,
     *         or an empty list if no path exists
     * @throws NullPointerException if house, start or destination is null
     */
    @Override
    public List<Room> findPath(House house, Room start, Room destination) {
        Objects.requireNonNull(house);
        Objects.requireNonNull(start);
        Objects.requireNonNull(destination);

        if (start.equals(destination)) {return List.of(start);}

        Queue<Room> queue = new ArrayDeque<>();
        Set<Room> visited = new HashSet<>();
        Map<Room, Room> previous = new HashMap<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Room current = queue.remove();

            for (Room adjacent : house.getAdjacentRooms(current)) {
                if (visited.contains(adjacent)) {
                    continue;
                }
                visited.add(adjacent);
                previous.put(adjacent, current);

                if (adjacent.equals(destination)) {
                    return buildPath(previous, start, destination);
                }
                queue.add(adjacent);
            }
        }

        return List.of();
    }

    /**
     * Reconstructs the path from the destination back to the starting room
     * using the predecessor relationships produced by the search.
     * The reconstructed path is reversed before being returned so that
     * rooms are ordered from start to destination.
     * @param previous map associating each visited room with its predecessor
     * @param start starting room of the path
     * @param destination destination room of the path
     * @return the reconstructed path ordered from start to destination
     */
    private List<Room> buildPath(Map<Room, Room> previous, Room start, Room destination) {
        List<Room> path = new ArrayList<>();
        Room current = destination;

        while (current != null) {
            path.add(current);
            if (current.equals(start)) {
                break;
            }
            current = previous.get(current);
        }
        Collections.reverse(path);
        return path;
    }
}
