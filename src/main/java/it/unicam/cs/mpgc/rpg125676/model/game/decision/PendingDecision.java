package it.unicam.cs.mpgc.rpg125676.model.game.decision;

import java.io.Serializable;

/**
 * Represents a player decision that must be resolved
 * before the current turn can be completed.
 */
public interface PendingDecision extends Serializable {

    /**
     * Returns the type of this decision.
     */
    DecisionType getType();

    /**
     * Returns the message that should be shown to the player.
     */
    String getPrompt();
}
