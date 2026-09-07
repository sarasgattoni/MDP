package it.unicam.cs.mpgc.rpg125676.model.game.engine;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.command.GameAction;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.PlayerAttribute;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decision.MemoryAttributeDecision;
import it.unicam.cs.mpgc.rpg125676.model.game.decision.PendingDecision;
import it.unicam.cs.mpgc.rpg125676.model.game.turn.TurnProcessor;
import java.util.Objects;

/**
 * Provides the default implementation of the game engine.
 * The engine coordinates the execution of player actions and
 * delegates the completion of consumed turns to the
 * {@link TurnProcessor}.
 */
public class DefaultGameEngine implements GameEngine {

    private final GameState state;
    private final TurnProcessor turnProcessor;
    private ActionResult suspendedActionResult;
    private GamePhase suspendedStartingPhase;

    /**
     * Creates a game engine for the supplied state and turn processor.
     *
     * @param state game state managed by the engine
     * @param turnProcessor processor responsible for completing game turns
     * @throws NullPointerException if state or turnProcessor is null
     */
    public DefaultGameEngine(GameState state, TurnProcessor turnProcessor) {
        this.state = Objects.requireNonNull(state);
        this.turnProcessor = Objects.requireNonNull(turnProcessor);
    }

    @Override
    public GameState getState() {
        return state;
    }

    /**
     * Executes the supplied action against the current game state.
     * Rejected actions are returned immediately without completing
     * the turn. If the action requires a pending decision, the turn
     * is suspended until that decision is resolved.
     * Otherwise, the resulting turn is completed through the
     * configured {@link TurnProcessor}.
     *
     * @param action action to execute
     * @return the result produced by the action
     * @throws NullPointerException if action is null
     */
    @Override
    public ActionResult execute(GameAction action) {
        Objects.requireNonNull(action);
        if (suspendedActionResult != null) {
            return ActionResult.rejected("The pending decision must be resolved first.");
        }
        GamePhase startingPhase = state.getPhase();
        ActionResult result = action.execute(state);
        if (!result.consumesTurn()) {
            return result;
        }
        if (state.hasPendingDecision()) {
            suspendTurn(result, startingPhase);
            return result;
        }
        turnProcessor.completeTurn(result, startingPhase);
        return result;
    }

    /**
     * Resolves the currently pending memory attribute decision.
     * The selected attribute receives the configured memory bonus.
     * The pending decision is then cleared and the previously
     * suspended turn is completed.
     *
     * @param attribute player attribute selected for the bonus
     * @return message describing the applied attribute increase
     * @throws NullPointerException if attribute is null
     * @throws IllegalStateException if no decision is pending,
     *         if the pending decision is not a memory attribute decision
     *         or if no suspended turn is available
     */
    @Override
    public String resolveMemoryDecision(PlayerAttribute attribute) {
        Objects.requireNonNull(attribute);
        PendingDecision pendingDecision = state.getPendingDecision().orElseThrow(() -> new IllegalStateException("There is no pending decision"));
        if (!(pendingDecision instanceof MemoryAttributeDecision decision)) {
            throw new IllegalStateException("The pending decision is not " + "a memory attribute decision");
        }
        ensureSuspendedTurnExists();
        decision.resolve(state.getPlayer(), attribute);
        state.clearPendingDecision();
        ActionResult actionResult = suspendedActionResult;
        GamePhase startingPhase = suspendedStartingPhase;
        clearSuspendedTurn();

        turnProcessor.completeTurn(actionResult, startingPhase);
        return formatDecisionMessage(attribute, decision.getBonus());
    }

    /**
     * Stores the information required to complete the current
     * turn after a pending decision has been resolved.
     *
     * @param result result of the action that suspended the turn
     * @param startingPhase phase in which the action was executed
     * @throws NullPointerException if result or startingPhase is null
     */
    private void suspendTurn(ActionResult result, GamePhase startingPhase) {
        suspendedActionResult = Objects.requireNonNull(result);
        suspendedStartingPhase = Objects.requireNonNull(startingPhase);
    }

    private void clearSuspendedTurn() {
        suspendedActionResult = null;
        suspendedStartingPhase = null;
    }

    /**
     * Verifies that a suspended turn is currently available.
     *
     * @throws IllegalStateException if no suspended turn exists
     */
    private void ensureSuspendedTurnExists() {
        if (suspendedActionResult == null || suspendedStartingPhase == null) {
            throw new IllegalStateException("There is no suspended turn");
        }
    }

    /**
     * Creates the message describing the attribute bonus obtained
     * from a memory decision.
     *
     * @param attribute attribute that received the bonus
     * @param bonus amount added to the attribute
     * @return formatted decision result message
     */
    private String formatDecisionMessage(PlayerAttribute attribute, int bonus) {
        String attributeName =
                switch (attribute) {
                    case COMPOSURE -> "Composure";
                    case CAUTION -> "Caution";
                };

        return attributeName + " increased by " + bonus + ".";
    }
}
