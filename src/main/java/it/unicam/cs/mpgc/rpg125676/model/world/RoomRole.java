package it.unicam.cs.mpgc.rpg125676.model.world;

/**
 * Identifies the structural role of a room inside the house.
 */
public enum RoomRole {
    /** Room in which the player begins the game. */
    START,
    /** Ordinary room with no special structural role. */
    STANDARD,
    /** Connecting room used mainly as a navigation point. */
    HUB,
    /** Room where the player can go once all memories are found and win the game. */
    FINAL
}
