package it.unicam.cs.mpgc.rpg125676.model.game.end;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;

import java.util.Objects;

/**
 * Evaluates whether the current game session has reached
 * an end condition.
 * The evaluator inspects the current game state and updates
 * the game status when a defeat condition is satisfied.
 * No evaluation is performed if the game has already ended.
 */
public class GameEndEvaluator {

    /**
     * Evaluates the current game state and applies any end-game condition.
     * If the game is still in progress and the player's lucidity
     * has reached zero, the session is marked as lost.
     * @param state current game state to evaluate
     * @throws NullPointerException if state is null
     */
    public void evaluate(GameState state) {
        Objects.requireNonNull(state);
        if (state.isFinished()) {
            return;
        }
        if (!state.getPlayer().getStats().isAlive()) {
            state.lose();
        }
    }
}
