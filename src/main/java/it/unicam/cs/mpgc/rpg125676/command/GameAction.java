package it.unicam.cs.mpgc.rpg125676.command;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;

/**
 * Defines a game action that can be requested and executed
 * during a game session.
 *
 * Each action provides a human-readable name and applies its
 * specific behavior to the current {@link GameState}, producing
 * an {@link ActionResult} that describes the outcome.
 */
public interface GameAction {

    String getName();
    /**
     * Executes the action against the current game state.
     *
     * @param state current game state
     * @return action result
     */
    ActionResult execute(GameState state);
}
