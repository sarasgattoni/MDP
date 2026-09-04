package it.unicam.cs.mpgc.rpg125676.model.world;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

/**
 * Defines the structure of the house as an undirected graph.
 */
public interface House extends Serializable {

    /**
     * Returns all rooms in the house.
     */
    Collection<Room> getRooms();

    /**
     * Finds a room by its unique identifier.
     */
    Room getRoom(String roomId);

    /**
     * Returns all rooms directly connected to the given room.
     */
    Set<Room> getAdjacentRooms(Room room);

    /**
     * Checks whether two rooms are directly connected.
     */
    boolean areConnected(Room first, Room second);
}
