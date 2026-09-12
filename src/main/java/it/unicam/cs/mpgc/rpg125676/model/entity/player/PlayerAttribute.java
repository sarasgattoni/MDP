package it.unicam.cs.mpgc.rpg125676.model.entity.player;

/**
 * Defines the player attributes that can be increased during the game.
 *
 * COMPOSURE improves the player's ability to face the Presence.
 * CAUTION improves the player's ability to act quietly when moving or searching.
 */
public enum PlayerAttribute {
    /**
     * Represents the player's ability to remain calm during confrontations.
     */
    COMPOSURE,

    /**
     * Represents the player's ability to search silently and escape from the presence.
     */
    CAUTION
}
