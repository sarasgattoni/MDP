package it.unicam.cs.mpgc.rpg125676.model.game.decoy;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.Objects;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a decoy that has been placed inside the house
 * and is currently active.
 * The decoy is associated with the room in which it was placed
 * and produces noise for a limited number of subsequent turns.
 * The placement turn itself is silent. Noise production starts
 * from the following completed game turn.
 */
public class ActiveDecoy implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private final Room room;
    private int remainingNoiseTurns;
    private boolean justPlaced;

    /**
     * Creates an active decoy in the specified room.
     *
     * @param room room in which the decoy is placed
     * @param duration number of turns during which the decoy can produce noise
     * @throws NullPointerException if room is null
     * @throws IllegalArgumentException if duration is not positive
     */
    public ActiveDecoy(Room room, int duration) {
        if (duration <= 0) {throw new IllegalArgumentException("Decoy duration must be positive");}
        this.room = Objects.requireNonNull(room);
        this.remainingNoiseTurns = duration;
        this.justPlaced = true;
    }

    public Room getRoom() {
        return room;
    }

    /**
     * Returns the number of remaining turns during which the decoy
     * can still produce noise.
     *
     * @return the remaining noise-producing turns
     */

    public int getRemainingNoiseTurns() {
        return remainingNoiseTurns;
    }

    /**
     * Advances the decoy state by one completed game turn.
     * If the decoy has just been placed, the placement state is cleared
     * and no noise is produced. Otherwise, one remaining noise turn is
     * consumed when available.
     *
     * @return true if the decoy produces noise during this turn,
     *         false otherwise
     */
    public boolean consumeNoiseTurn() {
        if (justPlaced) {
            justPlaced = false;
            return false;
        }
        if (remainingNoiseTurns <= 0) {
            return false;
        }
        remainingNoiseTurns--;
        return true;
    }

    public boolean isExpired() {
        return !justPlaced && remainingNoiseTurns == 0;
    }
    public boolean isJustPlaced() {
        return justPlaced;
    }
}