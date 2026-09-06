package it.unicam.cs.mpgc.rpg125676.model.game.turn;

import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the total noise generated during a game turn.
 * The record stores the total noise amount and the room that
 * produced the most recent noise during the turn.
 * A silent turn has no noise source, while a noisy turn must
 * always identify its last source.
 *
 * @param amount total amount of noise generated during the turn
 * @param lastSource optional room containing the latest noise source
 */
public record TurnNoise(int amount, Optional<Room> lastSource) {

    /**
     * Validates the values used to create the turn noise.
     *
     * @throws NullPointerException if lastSource is null
     * @throws IllegalArgumentException if amount is negative,
     *         if a silent turn has a source or if a noisy turn
     *         has no source
     */
    public TurnNoise {
        Objects.requireNonNull(lastSource);
        if (amount < 0) {throw new IllegalArgumentException("Noise cannot be negative");}
        if (amount == 0 && lastSource.isPresent()) {throw new IllegalArgumentException("Silent turn cannot have a noise source");}
        if (amount > 0 && lastSource.isEmpty()) {throw new IllegalArgumentException("Noisy turn must have a noise source");}
    }

    /**
     * Creates a noise representation for a completely silent turn.
     *
     * @return a turn noise with zero amount and no source
     */
    public static TurnNoise silent() {
        return new TurnNoise(0, Optional.empty());
    }

    /**
     * Creates a noisy turn with the supplied amount and latest source.
     *
     * @param amount total amount of noise generated during the turn
     * @param lastSource room containing the latest noise source
     * @return the corresponding turn noise
     * @throws NullPointerException if lastSource is null
     * @throws IllegalArgumentException if amount is not valid
     *         for a noisy turn
     */
    public static TurnNoise of(int amount, Room lastSource) {
        return new TurnNoise(amount, Optional.of(Objects.requireNonNull(lastSource)));
    }
}
