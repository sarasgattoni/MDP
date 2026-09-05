package it.unicam.cs.mpgc.rpg125676.model.game;

/**
 * Defines the current phase of gameplay.
 * The phase determines which actions are available to the player
 * according to the current position of the Presence.
 */
public enum GamePhase {
    /**
     * The player is exploring the house normally.
     */
    EXPLORATION,
    /**
     * The player is sharing a room with the Presence
     * and must resolve a confrontation.
     */
    CONFRONTATION
}
