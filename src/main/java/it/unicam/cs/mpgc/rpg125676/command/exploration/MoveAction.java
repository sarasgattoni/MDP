package it.unicam.cs.mpgc.rpg125676.command.exploration;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;

import java.util.Objects;

/**
 * Moves the player to a selected adjacent room.
 * The destination must be connected to the current room. The final
 * room cannot be entered until the player has recovered all memories.
 * A successful movement marks the destination as visited, updates
 * the current game phase and generates the configured movement noise.
 */
public class MoveAction extends AbstractExplorationAction {

    private final Room destination;

    public MoveAction(Room destination) {
        this.destination = Objects.requireNonNull(destination);
    }

    @Override
    public String getName() {
        return "Move";
    }

    /**
     * Attempts to move the player to the configured destination.
     *
     * @param state current game state
     * @return a successful movement result or a rejection result
     *         if the destination cannot be entered
     */
    @Override
    protected ActionResult perform(GameState state) {
        Player player = state.getPlayer();
        Room currentRoom = player.getCurrentRoom();

        if (!state.getHouse().areConnected(currentRoom, destination)) {
            return ActionResult.rejected("The destination is not adjacent " + "to the current room.");
        }
        if (destination.getRole() == RoomRole.FINAL && !player.hasAllMemories()) {
            return ActionResult.rejected("The locked room cannot be entered " + "before recovering all memories.");
        }

        player.moveTo(destination);
        destination.markAsVisited();
        state.synchronizePhaseWithPositions();
        int movementNoise = state.getSettings().noise().movementNoise();

        return ActionResult.success(movementNoise, destination, "You moved to " + destination.getName() + ".");
    }
}
