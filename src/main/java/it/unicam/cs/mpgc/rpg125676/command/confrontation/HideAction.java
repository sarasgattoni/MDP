package it.unicam.cs.mpgc.rpg125676.command.confrontation;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Attempts to hide from the Presence using the player's Caution.
 * The action combines a dice roll with the player's Caution
 * and compares the result with the configured confrontation threshold.
 * On success, the Presence moves to a randomly selected adjacent
 * room and its attention is reduced. On failure, the player loses
 * Lucidity and gains a future recovery opportunity.
 */
public class HideAction extends AbstractConfrontationAction {

    private final DiceRoller diceRoller;
    private final RandomGenerator random;

    /**
     * Creates a Hide action using the supplied dice roller.
     * The default random generator is used to select the room
     * to which the Presence moves after a successful hide.
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
     * Resolves the attempt to hide from the Presence.
     * The dice roll is added to the player's Caution and compared
     * with the configured value. On success, the Presence is moved
     * to a random adjacent room and its attention is reduced.
     * On failure, the configured Lucidity damage is applied and
     * a recovery opportunity is enabled.
     *
     * @param state current game state
     * @return a silent success or failure result according to the check
     */
    @Override
    protected ActionResult perform(GameState state) {
        int roll = diceRoller.roll();
        int total = roll + state.getPlayer().getStats().getCaution();
        int threshold = state.getSettings().confrontation().hideThreshold();
        if (total >= threshold) {
            List<Room> adjacentRooms = List.copyOf(state.getHouse().getAdjacentRooms(state.getPresence().getCurrentRoom()));
            Room destination = adjacentRooms.get(random.nextInt(adjacentRooms.size()));
            state.getPresence().moveTo(destination);
            if (!state.isDecoyActive()) {
                state.getPresence().decreaseAttention(state.getSettings().confrontation().hideAttentionDecrease());
            }
            state.synchronizePhaseWithPositions();

            return ActionResult.silentSuccess("You disappeared from sight. " + "The Presence moves away.");
        }
        state.getPlayer().getStats().loseLucidity(state.getSettings().confrontation().damage());
        state.enableRecovery();

        return ActionResult.silentFailure("You failed to hide. " + "The Presence finds you. You lose " + state.getSettings().confrontation().damage() + " Lucidity.");
    }
}