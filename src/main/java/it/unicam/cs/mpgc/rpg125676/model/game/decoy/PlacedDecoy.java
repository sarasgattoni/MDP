package it.unicam.cs.mpgc.rpg125676.model.game.decoy;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a decoy placed inside the house.
 * The decoy remains silent until explicitly activated. Once the Presence
 * reaches its room, the decoy keeps it there for a limited number of turns.
 */
public class PlacedDecoy implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Room room;
    private int remainingHoldTurns;
    private boolean ringing;
    private boolean presenceReached;

    public PlacedDecoy(Room room, int holdTurns) {
        if (holdTurns <= 0) {
            throw new IllegalArgumentException("Decoy hold duration must be positive");
        }

        this.room = Objects.requireNonNull(room);
        this.remainingHoldTurns = holdTurns;
        this.ringing = false;
        this.presenceReached = false;
    }

    public Room getRoom() {
        return room;
    }

    public int getRemainingHoldTurns() {
        return remainingHoldTurns;
    }

    public boolean isRinging() {
        return ringing;
    }

    public boolean hasPresenceReached() {
        return presenceReached;
    }

    /**
     * Starts the decoy ringing.
     *
     * @throws IllegalStateException if the decoy is already ringing
     */
    public void activate() {
        if (ringing) {
            throw new IllegalStateException("The decoy is already ringing");
        }
        ringing = true;
    }

    /**
     * Records that the Presence has reached the decoy room.
     *
     * @throws IllegalStateException if the decoy has not been activated
     */
    public void markPresenceReached() {
        if (!ringing) {
            throw new IllegalStateException("An inactive decoy cannot attract the Presence");
        }
        presenceReached = true;
    }

    /**
     * Consumes one turn of the holding period after the Presence has reached
     * the decoy room.
     */
    public void consumeHoldTurn() {
        if (ringing && presenceReached && remainingHoldTurns > 0) {
            remainingHoldTurns--;
        }
    }

    public boolean isExpired() {
        return ringing && presenceReached && remainingHoldTurns == 0;
    }
}
