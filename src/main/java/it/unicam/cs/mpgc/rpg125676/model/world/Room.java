package it.unicam.cs.mpgc.rpg125676.model.world;

import it.unicam.cs.mpgc.rpg125676.model.content.RoomContent;

import java.util.Objects;
import java.util.Optional;
import java.io.Serial;
import java.io.Serializable;

/** Represents a room inside the house explored during the game.
 * A room is identified by a unique {@code id} and has a human-readable
 * {@code name}. It also stores the structural and gameplay information
 * required to describe its role inside the house.
 * Each room defines:
 * whether it can be searched by the player;
 * its semantic {@link RoomRole};
 * whether the Presence is allowed to start the game in it;
 * whether the room has already been searched;
 * whether the player has already visited it;
 * the {@link RoomContent} associated with the room.
 */
public class Room implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final boolean searchable;
    private final RoomRole role;
    private final boolean presenceStartAllowed;

    private boolean searched;
    private boolean visited;
    private RoomContent content;
    /**
     * Creates a fully configured room.
     *  @param id unique identifier of the room
     *  @param name human-readable room name
     *  @param searchable whether the room can be searched
     *  @param role structural role of the room
     *  @param presenceStartAllowed whether the Presence may start in this room
     *
     *  @throws IllegalArgumentException if {@code id} or {@code name}is null or blank.
     *  @throws NullPointerException if {@code role} is null
     */
    public Room(String id, String name, boolean searchable, RoomRole role, boolean presenceStartAllowed) {
        if (id == null || id.isBlank()) {throw new IllegalArgumentException("Room id cannot be blank");}

        if (name == null || name.isBlank()) {throw new IllegalArgumentException ("Room name cannot be blank");}
        this.id = id;
        this.name = name;
        this.searchable = searchable;
        this.role = Objects.requireNonNull(role);
        this.presenceStartAllowed = presenceStartAllowed;
        this.searched = false;
        this.visited = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public RoomRole getRole() {
        return role;
    }

    public boolean isPresenceStartAllowed() {
        return presenceStartAllowed;
    }

    public boolean isSearched() {
        return searched;
    }

    public void markAsSearched() {
        if (!searchable) {throw new IllegalStateException("A non-searchable room cannot be marked as searched");}
        searched = true;
    }

    public Optional<RoomContent> getContent() {
        return Optional.ofNullable(content);
    }

    public void setContent(RoomContent content) {
        if (!searchable) {throw new IllegalStateException("Cannot assign content to a non-searchable room");}

        if (this.content != null) {throw new IllegalStateException("Room already contains content");}
        this.content = Objects.requireNonNull(content);
    }
    public boolean isVisited() {
        return visited;
    }

    public void markAsVisited() {
        visited = true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {return true;}
        if (!(object instanceof Room other)) {return false;}

        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}