package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.ActiveDecoy;

/**
 * Places one collected decoy in the player's current room.
 * The action requires an available decoy in the player's inventory
 * and no other active decoy in the game.
 * A successfully placed decoy remains silent during the placement
 * turn and starts producing noise on subsequent turns.
 */
public class PlaceDecoyAction extends AbstractExplorationAction {

    @Override
    public String getName() {
        return "Place Decoy";
    }

    /**
     * Attempts to place a decoy in the player's current room.
     * The decoy is removed from the player's inventory and registered
     * in the game state using the configured duration.
     *
     * @param state current game state
     * @return a silent successful result if the decoy is placed,
     *         otherwise a rejection result
     * @throws IllegalStateException if an available decoy cannot
     *         be consumed from the player's inventory
     */
    @Override
    protected ActionResult perform(GameState state) {
        Player player = state.getPlayer();

        if (!player.hasDecoy()) {return ActionResult.rejected("You do not have a decoy.");
        }
        if (state.hasActiveDecoy()) {return ActionResult.rejected("Another decoy is already active.");
        }
        boolean consumed = player.consumeDecoy();
        if (!consumed) {
            throw new IllegalStateException("Unable to consume the player's decoy");
        }

        ActiveDecoy activeDecoy = new ActiveDecoy(player.getCurrentRoom(), state.getSettings().decoy().duration());
        state.placeDecoy(activeDecoy);

        return ActionResult.silentSuccess("You placed the alarm clock in " + player.getCurrentRoom().getName() + ". It will start ringing next turn.");
    }
}
