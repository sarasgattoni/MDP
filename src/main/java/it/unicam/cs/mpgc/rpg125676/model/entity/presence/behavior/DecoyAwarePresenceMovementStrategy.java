package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.decoy.PlacedDecoy;

import java.util.Objects;

/**
 * Adds decoy behavior to another Presence movement strategy.
 * When the Presence reaches a ringing decoy, its movement is suspended for the configured
 * holding duration.
 */
public class DecoyAwarePresenceMovementStrategy implements PresenceMovementStrategy {

    private final PresenceMovementStrategy defaultMovement;

    public DecoyAwarePresenceMovementStrategy(PresenceMovementStrategy defaultMovement) {
        this.defaultMovement = Objects.requireNonNull(defaultMovement);
    }

    @Override
    public void move(GameState state) {
        Objects.requireNonNull(state);

        PlacedDecoy decoy = state.getPlacedDecoy().orElse(null);

        if (decoy == null || !decoy.isRinging()) {
            defaultMovement.move(state);
            return;
        }

        if (state.getPresence().getCurrentRoom().equals(decoy.getRoom())) {
            holdPresence(state, decoy);
            return;
        }

        defaultMovement.move(state);

        if (state.getPresence().getCurrentRoom().equals(decoy.getRoom())) {
            decoy.markPresenceReached();
        }
    }

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
