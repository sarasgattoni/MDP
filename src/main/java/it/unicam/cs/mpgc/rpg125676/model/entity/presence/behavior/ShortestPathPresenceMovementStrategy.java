package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.entity.presence.Presence;
import it.unicam.cs.mpgc.rpg125676.model.entity.presence.PresenceState;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.movement.PathFinder;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.List;
import java.util.Objects;

/**
 * Implements the movement behavior of the Presence using the shortest
 * available path toward the current noise target.
 * The strategy determines the current Presence state from its attention
 * level, obtains the corresponding number of movement steps and uses a
 * {@link PathFinder} to calculate the path toward the target room.
 *
 * Movement stops immediately if the Presence reaches the player's room.
 */
public class ShortestPathPresenceMovementStrategy implements PresenceMovementStrategy {

    private final PathFinder pathFinder;
    private final PresenceStateResolver stateResolver;

    /**
     * Creates a movement strategy with the components required to calculate
     * paths and determine Presence movement behavior.
     *
     * @param pathFinder component used to calculate the shortest path
     *                   between rooms
     * @param stateResolver component used to determine the Presence state
     *                      and its allowed movement steps
     * @throws NullPointerException if pathFinder or stateResolver is null
     */
    public ShortestPathPresenceMovementStrategy(PathFinder pathFinder, PresenceStateResolver stateResolver) {
        this.pathFinder = Objects.requireNonNull(pathFinder);
        this.stateResolver = Objects.requireNonNull(stateResolver);
    }
    /**
     * Moves the Presence toward the current noise target according to
     * its attention level.
     * No movement is performed when the player already shares the same
     * room with the Presence, when no noise target is available or when
     * the current Presence state allows no movement.
     * When movement is possible, the shortest path to the target is
     * calculated and followed for the allowed number of steps.
     *
     * @param state current game state
     * @throws NullPointerException if state is null
     */
    @Override
    public void move(GameState state) {
        Objects.requireNonNull(state);
        if (state.isPlayerWithPresence()) {
            return;
        }

        Room target =
                state
                        .getNoiseTarget()
                        .orElse(null);

        if (target == null) {return;}

        Presence presence = state.getPresence();

        PresenceState presenceState = stateResolver.resolve(presence.getAttention());

        int movementSteps = stateResolver.movementSteps(presenceState);

        if (movementSteps <= 0) {return;}

        List<Room> path = pathFinder.findPath(state.getHouse(), presence.getCurrentRoom(), target);

        moveAlongPath(state, path, movementSteps);
    }

    /**
     * Moves the Presence along the supplied path for the allowed number
     * of steps.
     *
     * The movement is limited both by the requested number of steps and
     * by the length of the path. It stops early if the Presence reaches
     * the player's current room.
     *
     * @param state current game state
     * @param path path to follow
     * @param movementSteps maximum number of steps to perform
     */
    private void moveAlongPath(GameState state, List<Room> path, int movementSteps) {
        if (path.size() <= 1) {return;}

        Presence presence = state.getPresence();

        int actualSteps = Math.min(movementSteps, path.size() - 1);

        for (int index = 1; index <= actualSteps; index++) {
            presence.moveTo(path.get(index));

            if (state.isPlayerWithPresence()) {
                break;
            }
        }
    }
}
