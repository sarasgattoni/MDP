package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;

/**
 * Performs the final action required to win the game.
 * The Presence's name can only be spoken inside the final room
 * after the player has recovered all required memories.
 * A successful execution immediately marks the game as won.
 */
public class SpeakNameAction extends AbstractExplorationAction {

    @Override
    public String getName() {
        return "Speak Name";
    }

    /**
     * Attempts to complete the game by speaking the Presence's name.
     *
     * @param state current game state
     * @return a successful result if the victory conditions are met,
     *         otherwise a rejection result
     */
    @Override
    protected ActionResult perform(GameState state) {
        Player player = state.getPlayer();
        Room room = player.getCurrentRoom();
        if (room.getRole() != RoomRole.FINAL) {
            return ActionResult.rejected("The name can only be spoken " + "inside the locked room.");
        }
        if (!player.hasAllMemories()) {
            return ActionResult.rejected("You still do not know the Presence's identity.");
        }
        state.win();
        return ActionResult.silentSuccess("You spoke the name of the Presence. " + "The house finally falls silent.");
    }
}
