package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;

/**
 * Defines how the Presence moves during a turn.
 */
public interface PresenceMovementStrategy {

    void move(GameState state);
}
