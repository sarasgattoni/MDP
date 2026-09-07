package it.unicam.cs.mpgc.rpg125676.command.confrontation;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.command.GameAction;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;

import java.util.Objects;

/**
 * Provides the common base implementation for actions that can
 * only be executed during a confrontation with the Presence.
 * Before executing the specific action behavior, the class verifies
 * that the game is still in progress, that no decision is pending
 * and that the current phase is confrontation.
 */
public abstract class AbstractConfrontationAction
        implements GameAction {

    /**
     * Executes the action after validating the common confrontation rules.
     * The action is rejected if the game has already ended, if a
     * decision is pending or if the current phase is not confrontation.
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
            return ActionResult.rejected("The pending decision must be resolved first.");
        }
        if (state.getPhase()!= GamePhase.CONFRONTATION) {
            return ActionResult.rejected("This action can only be performed " + "during a confrontation.");
        }

        return perform(state);
    }

    /**
     * Executes the behavior specific to this confrontation action.
     * This method is invoked only after the common confrontation
     * conditions have been successfully validated.
     *
     * @param state current game state
     * @return the result produced by the specific action
     */
    protected abstract ActionResult perform(GameState state);
}
