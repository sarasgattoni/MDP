package it.unicam.cs.mpgc.rpg125676.model.game.turn;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior.PresenceMovementStrategy;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.attention.AttentionPolicy;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.game.end.GameEndEvaluator;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import java.util.Objects;

/**
 * Coordinates the completion of a game turn.
 * The processor combines the result of the executed action with
 * any noise produced by an active decoy and applies the rules
 * associated with the phase in which the turn started.
 * During exploration it updates Presence attention and movement.
 * During confrontation the standard attention and movement rules
 * are suspended. The processor then evaluates end-game conditions,
 * synchronizes the game phase and advances the turn counter.
 */
public class TurnProcessor {

    private final GameState state;
    private final AttentionPolicy attentionPolicy;
    private final PresenceMovementStrategy presenceMovement;
    private final GameEndEvaluator gameEndEvaluator;

    /**
     * Creates a turn processor with the components required
     * to complete game turns.
     *
     * @param state game state to update
     * @param attentionPolicy policy used to update Presence attention
     * @param presenceMovement strategy used to move the Presence
     * @param gameEndEvaluator evaluator used to detect end-game conditions
     * @throws NullPointerException if any argument is null
     */
    public TurnProcessor(GameState state, AttentionPolicy attentionPolicy, PresenceMovementStrategy presenceMovement, GameEndEvaluator gameEndEvaluator) {
        this.state = Objects.requireNonNull(state);
        this.attentionPolicy = Objects.requireNonNull(attentionPolicy);
        this.presenceMovement = Objects.requireNonNull(presenceMovement);
        this.gameEndEvaluator = Objects.requireNonNull(gameEndEvaluator);
    }

    /**
     * Completes the current turn after a game action has been executed.
     * The method calculates the total turn noise and applies the rules
     * associated with the phase in which the action was performed.
     * It then evaluates possible end-game conditions, updates the game
     * phase when necessary and advances the turn counter.
     * If the executed action has already ended the game, the remaining
     * turn rules are skipped and only the turn counter is advanced.
     *
     * @param actionResult result produced by the executed action
     * @param startingPhase phase in which the action was performed
     * @throws NullPointerException if actionResult or startingPhase is null
     */
    public void completeTurn(ActionResult actionResult, GamePhase startingPhase) {
        Objects.requireNonNull(actionResult);
        Objects.requireNonNull(startingPhase);

        if (state.isFinished()) {
            state.advanceTurn();
            return;
        }
        if (state.isDecoyActive()) {
            completeDecoyRules(startingPhase);
        } else {
            TurnNoise turnNoise = createTurnNoise(actionResult);
            if (startingPhase == GamePhase.EXPLORATION) {
                completeExplorationRules(turnNoise);
            } else {
                updateConfrontationNoiseTarget(turnNoise);
            }
        }
        gameEndEvaluator.evaluate(state);
        if (!state.isFinished()) {
            state.synchronizePhaseWithPositions();
        }
        state.advanceTurn();
    }

    /**
     * Applies the rules required to complete an exploration turn.
     * Presence attention is updated according to the generated noise.
     * If the game is still in progress, the Presence is then moved
     * according to the configured movement strategy.
     *
     * @param turnNoise total noise generated during the turn
     */
    private void completeExplorationRules(TurnNoise turnNoise) {
        attentionPolicy.apply(state, turnNoise);
        if (!state.isFinished()) {
            presenceMovement.move(state);
        }
    }
    /**
     * Calculates the total noise generated during the current turn.
     * During normal exploration, it applies Attention rules
     * and moves the Presence according to the configured movement strategy.
     * While a decoy is active, normal Attention updates are suspended and
     * Presence movement is controlled by the decoy aware strategy.
     * During confrontation, normal exploration rules are suspended.
     *
     * @param actionResult result of the executed player action
     * @return the total noise generated during the turn
     * @throws IllegalStateException if a noisy turn has no identifiable source
     */
    private TurnNoise createTurnNoise(ActionResult actionResult) {
        int totalNoise = actionResult.noise();
        Room lastNoiseSource = actionResult.noiseSource().orElse(null);
        if (totalNoise == 0) {
            return TurnNoise.silent();
        }
        if (lastNoiseSource == null) {
            throw new IllegalStateException("Noisy turn has no noise source");
        }
        return TurnNoise.of(totalNoise, lastNoiseSource);
    }

    /**
     * Updates the latest noise target during a confrontation turn.
     * The standard attention rule is suspended during confrontation,
     * but noise generated during the turn may still update the room
     * currently targeted by the Presence.
     *
     * @param turnNoise total noise generated during the turn
     */
    private void updateConfrontationNoiseTarget(TurnNoise turnNoise) {
        if (turnNoise.amount() <= 0) {
            return;
        }
        turnNoise.lastSource().ifPresent(state::setNoiseTarget);
    }

    /**
     *  Applies the temporary turn rules used while a decoy is active.
     * Attention remains unchanged and the decoy room stays as the
     * Presence's only noise target.
     *
     * @param startingPhase phase in which the turn started
     */
    private void completeDecoyRules(GamePhase startingPhase) {
        PlacedDecoy decoy = state.getPlacedDecoy().orElseThrow();
        state.setNoiseTarget(decoy.getRoom());
        if (startingPhase == GamePhase.EXPLORATION && !state.isFinished()) {
            presenceMovement.move(state);
        }
    }
}