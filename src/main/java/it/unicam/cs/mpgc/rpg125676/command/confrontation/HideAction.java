package it.unicam.cs.mpgc.rpg125676.command.confrontation;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Attempts to escape from the Presence using Caution.
 * On success, the player flees to a valid adjacent room while the Presence
 * remains in the confrontation room. Attention is not modified; on failure,
 * the player remains in place and loses Lucidity.
 */
public class HideAction extends AbstractConfrontationAction {

    private final DiceRoller diceRoller;
    private final RandomGenerator random;

    /**
     * Creates a Hide action using the specified dice roller.
     *
     * @param diceRoller dice roller used for the Caution check
     * @throws NullPointerException if diceRoller is null
     */
    public HideAction(DiceRoller diceRoller) {
        this.diceRoller = Objects.requireNonNull(diceRoller);
        this.random = RandomGenerator.getDefault();
    }

    @Override
    public String getName() {
        return "Hide";
    }

    /**
     * Resolves the attempt to escape from the Presence.
     * A successful Caution check moves the player to a random valid adjacent
     * room and ends the confrontation. The Presence and its Attention remain
     * unchanged; a failed check damages the player's Lucidity and enables
     * one future Catch Breath recovery opportunity.
     *
     * @param state current game state
     * @return result of the hiding attempt
     */
    @Override
    protected ActionResult perform(GameState state) {
        Player player = state.getPlayer();

        int roll = diceRoller.roll();
        int total = roll + player.getStats().getCaution();
        int threshold = state.getSettings().confrontation().hideThreshold();

        if (total >= threshold) {
            List<Room> escapeRooms = findEscapeRooms(state, player);
            if (escapeRooms.isEmpty()) {
                throw new IllegalStateException("The player has no valid room to escape to");
            }
            Room destination = escapeRooms.get(random.nextInt(escapeRooms.size()));
            player.moveTo(destination);
            destination.markAsVisited();
            state.synchronizePhaseWithPositions();
            return ActionResult.silentSuccess("You slip away before the Presence can reach you. " + "You escaped to " + destination.getName() + ".");
        }
        player.getStats().loseLucidity(state.getSettings().confrontation().damage());
        state.enableRecovery();
        return ActionResult.silentFailure("You failed to hide. The Presence finds you. You lose " + state.getSettings().confrontation().damage() + " Lucidity.");
    }

    /**
     * Finds adjacent rooms that the player may enter while escaping.
     * The final locked room is excluded until all memories have been
     * recovered, preserving the normal movement restriction.
     *
     * @param state current game state
     * @param player player attempting to escape
     * @return valid adjacent escape rooms
     */
    private List<Room> findEscapeRooms(GameState state, Player player) {
        return state.getHouse()
                .getAdjacentRooms(player.getCurrentRoom())
                .stream()
                .filter(room -> room.getRole() != RoomRole.FINAL || player.hasAllMemories())
                .toList();
    }
}