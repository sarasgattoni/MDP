package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;
import it.unicam.cs.mpgc.rpg125676.model.movement.PathFinder;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.List;
import java.util.Objects;

/**
 * Adds decoy behavior to another Presence movement strategy.
 * While a decoy is ringing, the Presence moves toward it at its
 * maximum configured speed. Once it reaches the decoy room,
 * movement is suspended for the remaining hold duration.
 */
public class DecoyAwarePresenceMovementStrategy implements PresenceMovementStrategy {

    private final PresenceMovementStrategy defaultMovement;
    private final PathFinder pathFinder;

    /**
     * Creates a movement strategy that adds decoy behavior to the
     * supplied standard movement strategy.
     *
     * @param defaultMovement movement strategy used when no active decoy exists
     * @param pathFinder path finder used to reach the active decoy room
     * @throws NullPointerException if any dependency is null
     */
    public DecoyAwarePresenceMovementStrategy(PresenceMovementStrategy defaultMovement, PathFinder pathFinder) {
        this.defaultMovement = Objects.requireNonNull(defaultMovement);
        this.pathFinder = Objects.requireNonNull(pathFinder);
    }

    /**
     * Moves the Presence according to the current decoy state.
     * If no decoy is active, movement is delegated to the standard strategy.
     * Otherwise, the Presence is either moved toward the decoy room or kept
     * there while the decoy's hold duration is consumed.
     * If the Presence had previously reached the decoy but was moved away
     * by another game mechanic, its reached state is cleared so that the
     * decoy can attract it again.
     *
     * @param state current game state
     * @throws NullPointerException if state is null
     */
    @Override
    public void move(GameState state) {
        Objects.requireNonNull(state);
        PlacedDecoy decoy = state.getPlacedDecoy().orElse(null);
        if (decoy == null || !decoy.isRinging()) {
            defaultMovement.move(state);
            return;
        }
        Presence presence = state.getPresence();
        if (decoy.hasPresenceReached() && !presence.getCurrentRoom().equals(decoy.getRoom())) {
            decoy.releasePresence();
        }
        if (presence.getCurrentRoom().equals(decoy.getRoom())) {
            holdPresence(state, decoy);
            return;
        }
        moveTowardDecoy(state, decoy);
    }

    /**
     * Moves the Presence toward the active decoy using the shortest path.
     * Movement uses the maximum number of steps configured for the Presence,
     * independently of its current Attention. Movement stops early if the
     * Presence encounters the player; reaching the decoy marks the beginning
     * of its holding phase without consuming a hold turn.
     *
     * @param state current game state
     * @param decoy active decoy attracting the Presence
     */
    private void moveTowardDecoy(GameState state, PlacedDecoy decoy) {
        Presence presence = state.getPresence();
        List<Room> path = pathFinder.findPath(state.getHouse(), presence.getCurrentRoom(), decoy.getRoom());

        if (path.size() <= 1) {
            return;
        }
        int movementSteps = state.getSettings().presence().unleashedMovementSteps();
        int actualSteps = Math.min(movementSteps, path.size() - 1);
        for (int index = 1; index <= actualSteps; index++) {
            presence.moveTo(path.get(index));
            if (state.isPlayerWithPresence()) {
                break;
            }
        }
        if (presence.getCurrentRoom().equals(decoy.getRoom())) {
            decoy.markPresenceReached();
        }
    }

    /**
     * Keeps the Presence in the decoy room and advances the holding period.
     * The turn in which the Presence first reaches the decoy does not consume
     * one of the configured hold turns. Each following movement resolution
     * consumes one hold turn; when no hold turns remain, the decoy is removed
     * from the game state and normal Presence behavior can resume on the
     * following turn.
     *
     * @param state current game state
     * @param decoy decoy currently holding the Presence
     */
    private void holdPresence(GameState state, PlacedDecoy decoy) {
        if (!decoy.hasPresenceReached()) {
            decoy.markPresenceReached();
            return;
        }
        decoy.consumeHoldTurn();
        if (decoy.isExpired()) {
            state.clearPlacedDecoy();
        }
    }
}