package it.unicam.cs.mpgc.rpg125676.model.content;
/**
 * Defines the possible types of content that can be found inside a room.
 * Each value identifies a specific category of room content and can be
 * used to distinguish the behavior associated with that content.
 */
public enum ContentType {
    /**
     * Content representing a memory discovered by the player.
     */
    MEMORY,

    /**
     * Content that restores part of the player's lucidity.
     */
    MEDICINE,

    /**
     * Content that can be used to deceive the Presence.
     */
    DECOY,

    /**
     * Content for empty room.
     */
    EMPTY
}