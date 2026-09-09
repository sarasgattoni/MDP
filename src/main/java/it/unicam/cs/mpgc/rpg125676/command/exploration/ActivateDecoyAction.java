package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;

/**
 * Activates a previously placed decoy.
 * Activation is silent for the player, while the decoy starts producing
 * noise from the room in which it was placed.
 */
public class ActivateDecoyAction extends AbstractExplorationAction {

    @Override
    public String getName() {
        return "Activate Decoy";
    }

    /**
     * Activates the decoy currently placed in the house.
     * The player's activation is silent; the noise produced by the decoy
     * is handled later during turn resolution.
     *
     * @param state current game state
     * @return the result of the activation attempt
     */
    @Override
    protected ActionResult perform(GameState state) {
        PlacedDecoy decoy = state.getPlacedDecoy().orElse(null);
        if (decoy == null) {
            return ActionResult.rejected("There is no placed decoy to activate.");
        }
        if (decoy.isRinging()) {
            return ActionResult.rejected("The decoy is already ringing.");
        }
        decoy.activate();
        return ActionResult.silentSuccess("You activated the alarm clock in " + decoy.getRoom().getName() + ". It starts ringing.");
    }
}
