package it.unicam.cs.mpgc.rpg125676.model.world;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Provides the default implementation of the house structure.
 * The house stores all rooms by their unique identifier and maintains
 * the bidirectional connections between adjacent rooms.
 * The class is responsible for building and exposing the graph that
 * represents the playable environment.
 */
public class DefaultHouse implements House {

    private final Map<String, Room> rooms;
    private final Map<Room, Set<Room>> connections;

    /**
     * Creates an empty house with no rooms or connections.
     */
    public DefaultHouse() {
        this.rooms = new HashMap<>();
        this.connections = new HashMap<>();
    }

    /**
     * Adds a room to the house.
     * Each room must have a unique identifier. When the room is added,
     * an empty collection of adjacent rooms is created for it.
     *
     * @param room room to add
     * @throws NullPointerException if room is null
     * @throws IllegalArgumentException if another room with the same id
     *         already exists
     */
    public void addRoom(Room room) {
        Objects.requireNonNull(room);

        if (rooms.containsKey(room.getId())) {
            throw new IllegalArgumentException("A room with id '%s' already exists".formatted(room.getId()));
        }

        rooms.put(room.getId(), room);
        connections.put(room, new LinkedHashSet<>());
    }

    /**
     * Creates a bidirectional connection between two rooms.
     * Both rooms must already belong to the house. A room cannot be
     * connected to itself.
     *
     * @param firstRoomId identifier of the first room
     * @param secondRoomId identifier of the second room
     * @throws IllegalArgumentException if either room id is unknown
     *         or both identifiers refer to the same room
     */
    public void connectRooms(String firstRoomId, String secondRoomId) {
        Room first = getRoom(firstRoomId);
        Room second = getRoom(secondRoomId);

        if (first.equals(second)) {throw new IllegalArgumentException("A room cannot be connected to itself");}

        connections.get(first).add(second);
        connections.get(second).add(first);
    }

    /**
     * Returns all rooms contained in the house.
     *
     * @return an unmodifiable collection containing all rooms
     */
    @Override
    public Collection<Room> getRooms() {
        return Collections.unmodifiableCollection(rooms.values());
    }

    /**
     * Returns the room associated with the specified identifier.
     *
     * @param roomId identifier of the requested room
     * @return the room with the specified id
     * @throws IllegalArgumentException if no room with the given id exists
     */
    @Override
    public Room getRoom(String roomId) {
        Room room = rooms.get(roomId);

        if (room == null) {throw new IllegalArgumentException("Unknown room id: " + roomId);
        }

        return room;
    }

    /**
     * Returns the rooms directly connected to the specified room.
     * @param room room whose adjacent rooms are requested
     * @return an unmodifiable set containing the adjacent rooms
     * @throws NullPointerException if room is null
     * @throws IllegalArgumentException if room does not belong to this house
     */
    @Override
    public Set<Room> getAdjacentRooms(Room room) {
        Objects.requireNonNull(room);

        Set<Room> adjacentRooms = connections.get(room);

        if (adjacentRooms == null) {
            throw new IllegalArgumentException("Room does not belong to this house");
        }

        return Collections.unmodifiableSet(adjacentRooms);
    }

    /**
     * Checks whether two rooms are directly connected.
     *
     * @param first first room to check
     * @param second second room to check
     * @return true if the rooms are adjacent, false otherwise
     * @throws NullPointerException if either room is null
     */
    @Override
    public boolean areConnected(Room first, Room second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        Set<Room> adjacentRooms = connections.get(first);

        return adjacentRooms != null && adjacentRooms.contains(second);
    }
}