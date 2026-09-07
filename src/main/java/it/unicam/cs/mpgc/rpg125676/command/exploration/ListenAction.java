package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

/**
 * Allows the player to listen for the current location
 * of the Presence.
 * Listening generates no noise and reveals the room currently
 * occupied by the Presence. After a successful use, the configured
 * listening cooldown is started, meaning the player will have to wait a number of turns before
 * the action can be used again.
 */
public class ListenAction extends AbstractExplorationAction {

    @Override
    public String getName() {
        return "Listen";
    }

    /**
     * Reveals the current room of the Presence if listening
     * is currently available.
     *
     * @param state current game state
     * @return a silent successful result containing the Presence
     *         location, or a rejection result while Listen is on cooldown
     */
    @Override
    protected ActionResult perform(GameState state) {
        if (!state.canListen()) {
            int remaining = state.getListenCooldownRemaining();

            return ActionResult.rejected("You need to wait " + remaining + " more turn(s) before listening again.");
        }

        Room presenceRoom = state.getPresence().getCurrentRoom();
        state.startListenCooldown();
        return ActionResult.silentSuccess("You listen carefully. " + "The Presence is in " + presenceRoom.getName() + "."
        );
    }
}