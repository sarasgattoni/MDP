package it.unicam.cs.mpgc.rpg125676.model.content;


import java.io.Serializable;

/**
 * Defines the common contract for content that can be discovered
 * while searching a room.
 * Each content provides a type, a name and a description, and defines
 * the effect produced when it is discovered by the player.
 */
public interface RoomContent extends Serializable {

    ContentType getType();

    String getName();

    String getDescription();


}