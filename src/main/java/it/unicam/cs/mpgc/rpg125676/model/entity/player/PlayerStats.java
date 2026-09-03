package it.unicam.cs.mpgc.rpg125676.model.entity.player;

import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;

import java.util.Objects;
import java.io.Serial;
import java.io.Serializable;

/** Represents and manages the mutable statistics of a player.
*The class stores the three attributes that describe the player's current condition during a game:
 * Lucidity represents the player's remaining mental stability; when it reaches zero, the player is considered defeated.
 * Composure represents the player's ability to face the Presence during a confrontation.
 * Caution represents the player's ability to act quietly and avoid producing unnecessary noise.
 */
public class PlayerStats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final GameSettings.PlayerConfig config;

    private int lucidity;
    private int composure;
    private int caution;
    /**
     * Creates a new set of player statistics using the supplied configuration.
     *
     * The initial values for lucidity, composure and caution are read
     * directly from the provided {@link GameSettings.PlayerConfig}.
     *
     * @param config configuration defining the initial and maximum
     *               values of the player statistics
     *
     * @throws NullPointerException if {@code config} is {@code null}
     */
    public PlayerStats(GameSettings.PlayerConfig config) {
        this.config = Objects.requireNonNull(config);

        this.lucidity = config.initialLucidity();
        this.composure = config.initialComposure();
        this.caution = config.initialCaution();
    }

    public int getLucidity() {
        return lucidity;
    }

    public int getComposure() {
        return composure;
    }

    public int getCaution() {
        return caution;
    }

    public boolean isAlive() {
        return lucidity > 0;
    }
    /**
     * Reduces the player's lucidity by the specified amount.
     * The resulting value cannot fall below {@code 0}. Therefore,
     * excessive damage results in zero lucidity rather than a negative
     * value.
     * @param amount amount of lucidity to remove
     *
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void loseLucidity(int amount) {
        validatePositiveAmount(amount);

        lucidity = Math.max(0, lucidity - amount);
    }

    /**
     * Restores the player's lucidity by the specified amount.
     * The resulting value cannot exceed the maximum lucidity defined
     * by the player configuration.
     * @param amount amount of lucidity to restore
     *
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void recoverLucidity(int amount) {
        validatePositiveAmount(amount);

        lucidity = Math.min(config.maxLucidity(), lucidity + amount);
    }

    /**
     * Increases the player's composure.
     * The value is capped at the maximum attribute value defined
     * by the player configuration.
     * @param amount amount to add to composure
     *
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void increaseComposure(int amount) {
        validatePositiveAmount(amount);

        composure = Math.min(config.maxAttribute(), composure + amount);
    }
    /**
     * Increases the player's caution.
     * The value is capped at the maximum attribute value defined
     * by the player configuration.
     * @param amount amount to add to composure
     *
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void increaseCaution(int amount) {
        validatePositiveAmount(amount);

        caution = Math.min(
                config.maxAttribute(),
                caution + amount
        );
    }

    /**
     * Increases one of the player's selectable attributes.
     * The requested attribute determines which specialized operation
     * is executed:
     *     {@link PlayerAttribute#COMPOSURE} increases composure;
     *     {@link PlayerAttribute#CAUTION} increases caution.
     * This method provides a common entry point for mechanics in which
     * the player chooses which attribute to improve, such as the discovery
     * of a memory.
     * @param attribute attribute to increase
     * @param amount amount to add to the selected attribute
     *
     * @throws NullPointerException if {@code attribute} is {@code null}
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void increaseAttribute(PlayerAttribute attribute, int amount) {
        Objects.requireNonNull(attribute);
        switch (attribute) {
            case COMPOSURE ->
                    increaseComposure(amount);

            case CAUTION ->
                    increaseCaution(amount);
        }
    }

    private void validatePositiveAmount(int amount) {
        if (amount < 0) {throw new IllegalArgumentException("Amount cannot be negative");}
    }
}