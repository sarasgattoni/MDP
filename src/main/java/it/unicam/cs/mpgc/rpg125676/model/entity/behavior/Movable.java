package it.unicam.cs.mpgc.rpg125676.model.entity.behavior;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;

/**
 * Represents an entity that can change its position inside the house.
 */
public interface Movable {

    /**
     * Returns the room currently occupied by this entity.
     *
     * @return current room
     */
    Room getCurrentRoom();

    /**
     * Moves this entity to another room.
     *
     * @param destination destination room
     */
    void moveTo(Room destination);
}