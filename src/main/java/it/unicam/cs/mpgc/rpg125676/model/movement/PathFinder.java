package it.unicam.cs.mpgc.rpg125676.model.movement;

import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.List;

/**
 * Defines a strategy for finding a path between two rooms of a house.
 */
public interface PathFinder {

    /**
     * Finds a path from the starting room to the destination.
     *
     * @param house house containing the rooms and their connections
     * @param start starting room of the path
     * @param destination destination room to reach
     * @return the ordered path from start to destination, or an empty
     *         list if no path exists
     */
    List<Room> findPath(House house, Room start, Room destination);
}
