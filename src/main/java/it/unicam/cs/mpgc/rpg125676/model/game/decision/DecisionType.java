package it.unicam.cs.mpgc.rpg125676.model.game.decision;

/**
 * Defines the types of player decisions that may temporarily
 * interrupt normal game actions.
 * Each value identifies a specific decision that must be resolved
 * before the game can continue normally.
 */
public enum DecisionType {
    /**
     * Decision that allows the player to choose which attribute
     * to improve after discovering a memory.
     */
    MEMORY_ATTRIBUTE
}