package it.unicam.cs.mpgc.rpg125676.command.confrontation;

import it.unicam.cs.mpgc.rpg125676.command.ActionResult;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.dice.DiceRoller;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.Objects;

/**
 * Attempts to face the Presence using the player's Composure.
 * The action combines a dice roll with the player's Composure
 * and compares the result with the configured confrontation value.
 * On success, the Presence retreats and its attention is reset.
 * On failure, the player loses Lucidity and gains a future
 * recovery opportunity.
 */
public class FacePresenceAction extends AbstractConfrontationAction {

    private final DiceRoller diceRoller;

    /**
     * Creates a confrontation action using the supplied dice roller.
     *
     * @param diceRoller dice roller used for the Composure check
     * @throws NullPointerException if diceRoller is null
     */
    public FacePresenceAction(DiceRoller diceRoller) {
        this.diceRoller = Objects.requireNonNull(diceRoller);
    }

    @Override
    public String getName() {
        return "Face Presence";
    }

    /**
     * Resolves the attempt to face the Presence.
     * The dice roll is added to the player's Composure and compared
     * with the configured value. Success forces the Presence
     * to retreat, while failure causes the configured Lucidity loss.
     *
     * @param state current game state
     * @return a silent success or failure result according to the check
     */
    @Override
    protected ActionResult perform(GameState state) {
        int roll = diceRoller.roll();
        int total = roll + state.getPlayer().getStats().getComposure();
        int threshold = state.getSettings().confrontation().facePresenceThreshold();

        if (total >= threshold) {
            retreatPresence(state);
            if (!state.isDecoyActive()) {
                state.getPresence().resetAttention();
            }
            state.synchronizePhaseWithPositions();
            return ActionResult.silentSuccess("You held your ground. " + "The Presence recoils into the darkness.");
        }

        state.getPlayer().getStats().loseLucidity(state.getSettings().confrontation().damage());
        state.enableRecovery();

        return ActionResult.silentFailure("Your nerve breaks. " + "The Presence remains. You lose " + state.getSettings().confrontation().damage() + " Lucidity.");
    }

    /**
     * Moves the Presence away after a successful confrontation.
     * The normal retreat follows the recent movement history of the
     * Presence for the configured number of steps.
     * If the movement history is empty and the Presence would remain
     * in the same room as the player, an adjacent room is selected
     * as a fallback so that a successful confrontation always ends.
     *
     * @param state current game state
     * @throws IllegalStateException if the Presence cannot move away
     */
    private void retreatPresence(GameState state) {
        Presence presence = state.getPresence();
        int retreatSteps = state.getSettings().confrontation().retreatSteps();
        presence.retreat(retreatSteps);
        if (!state.isPlayerWithPresence()) {
            return;
        }

        Room currentRoom = presence.getCurrentRoom();
        Room fallbackRoom = state.getHouse().getAdjacentRooms(currentRoom).stream().findFirst().orElseThrow(() -> new IllegalStateException("The Presence cannot retreat from " + currentRoom.getName()));
        presence.moveTo(fallbackRoom);
    }
}
