package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;

/**
 * Allows the player to recover Lucidity after suffering
 * confrontation damage.
 * Recovery is available only when Presence attention is zero,
 * a recovery opportunity is active and the player's Lucidity
 * is below its maximum value.
 * A successful recovery consumes the current recovery opportunity.
 */
public class CatchBreathAction extends AbstractExplorationAction {

    @Override
    public String getName() {
        return "Catch Breath";
    }

    /**
     * Attempts to use the currently available recovery opportunity.
     * When all recovery conditions are satisfied, the configured
     * amount of Lucidity is restored and the recovery opportunity
     * is consumed.
     *
     * @param state current game state
     * @return a silent successful result if recovery is performed,
     *         otherwise a rejection result
     */
    @Override
    protected ActionResult perform(GameState state) {
        if (state.getPresence().getAttention() != 0) {

            return ActionResult.rejected("You cannot catch your breath " + "while the Presence is still alert.");
        }

        if (!state.isRecoveryAvailable()) {
            return ActionResult.rejected("You do not need to recover from " + "a recent confrontation.");
        }

        int currentLucidity = state.getPlayer().getStats().getLucidity();
        int maximumLucidity = state.getSettings().player().maxLucidity();

        if (currentLucidity >= maximumLucidity) {

            return ActionResult.rejected("Your Lucidity is already at its maximum.");
        }
        state.getPlayer().getStats().recoverLucidity(state.getSettings().player().catchBreathRecovery());
        state.consumeRecovery();

        return ActionResult.silentSuccess("You steady your breathing and recover " + state.getSettings().player().catchBreathRecovery() + " Lucidity.");
    }
}