package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.command.GameAction;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import java.util.Objects;

/**
 * Provides the common base implementation for actions that can
 * only be executed during the exploration phase.
 * Before executing the specific action behavior, the class verifies
 * that the game is still in progress, that no decision is pending
 * and that the current phase is exploration.
 */
public abstract class AbstractExplorationAction implements GameAction {

    /**
     * Executes the action after validating the common exploration rules.
     * The action is rejected if the game has already ended, if a
     * decision is pending or if the player is currently involved
     * in a confrontation.
     *
     * @param state current game state
     * @return the result produced by the action or a rejection result
     *         when the action cannot be performed
     * @throws NullPointerException if state is null
     */
    @Override
    public ActionResult execute(GameState state) {
        Objects.requireNonNull(state);

        if (state.isFinished()) {
            return ActionResult.rejected("The game has already ended.");
        }
        if (state.hasPendingDecision()) {
            return ActionResult.rejected("The pending decision must be resolved " + "before performing another action.");
        }

        if (state.getPhase() != GamePhase.EXPLORATION) {
            return ActionResult.rejected("This action cannot be performed " + "during a confrontation.");
        }

        return perform(state);
    }

    /**
     * Executes the behavior specific to this exploration action.
     * This method is invoked only after the common exploration
     * conditions have been successfully validated.
     *
     * @param state current game state
     * @return the result produced by the specific action
     */
    protected abstract ActionResult perform(GameState state);
}
