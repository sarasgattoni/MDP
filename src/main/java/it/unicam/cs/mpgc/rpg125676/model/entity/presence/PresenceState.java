package it.unicam.cs.mpgc.rpg125676.model.entity.presence;

/**
 * Defines the behavioral state of the Presence according to its
 * current attention level.
 *
 * Each state determines how actively the Presence reacts to noise
 * and how many rooms it may cross during a turn.
 */
public enum PresenceState {
    /**
     * Represents the lowest activity level: the Presence does not move toward the noise target.
     */
    DORMANT,

    /**
     * Represents an active hunting state: the Presence moves toward the current noise target.
     */
    HUNTING,

    /**
     * Represents the highest activity level: the Presence moves toward the noise target at maximum speed.
     */
    UNLEASHED
}
