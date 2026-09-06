package it.unicam.cs.mpgc.rpg125676.model.game.attention;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.turn.TurnNoise;

/**
 * Defines how the Presence attention changes
 * according to the noise generated during a turn.
 */
public interface AttentionPolicy {

    void apply(GameState state, TurnNoise noise);
}