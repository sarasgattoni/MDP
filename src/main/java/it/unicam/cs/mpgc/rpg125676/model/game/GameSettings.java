package it.unicam.cs.mpgc.rpg125676.model.game;

import java.util.Objects;
import java.io.Serializable;

/**
 * Stores all configurable balance parameters used by the game.
 * The settings are grouped into dedicated configuration records
 * according to their responsibility, such as player statistics,
 * Presence behavior, noise, confrontation and room contents.
 *
 * @param player configuration of player statistics and recovery values
 * @param presence configuration of Presence attention and movement
 * @param dice configuration of the game dice
 * @param noise configuration of noise values and attention reduction
 * @param search configuration of search-related rules
 * @param confrontation configuration of confrontation rules
 * @param decoy configuration of active decoys
 * @param content configuration of room content quantities
 */
public record GameSettings(
        PlayerConfig player,
        PresenceConfig presence,
        DiceConfig dice,
        NoiseConfig noise,
        SearchConfig search,
        ConfrontationConfig confrontation,
        DecoyConfig decoy,
        ContentConfig content
) implements Serializable {
    private static final int LISTEN_COOLDOWN_TURNS = 3;

    /**
     * Validates the configuration sections used to create the game settings.
     *
     * @throws NullPointerException if any configuration section is null
     */
    public GameSettings {
        Objects.requireNonNull(player);
        Objects.requireNonNull(presence);
        Objects.requireNonNull(dice);
        Objects.requireNonNull(noise);
        Objects.requireNonNull(search);
        Objects.requireNonNull(confrontation);
        Objects.requireNonNull(decoy);
        Objects.requireNonNull(content);
    }


    /**
     * Returns the standard configuration defined by the game rules.
     */
    public static GameSettings standard() {
        return new GameSettings(
                new PlayerConfig(
                        10,
                        10,
                        4,
                        3,
                        1,
                        10,
                        1,
                        1,
                        2
                ),
                new PresenceConfig(
                        0,
                        10,
                        4,
                        8,
                        0,
                        1,
                        2
                ),
                new DiceConfig(6),
                new NoiseConfig(
                        1,
                        2,
                        1
                ),
                new SearchConfig(8),
                new ConfrontationConfig(
                        8,
                        7,
                        2,
                        3
                ),
                new DecoyConfig(4),
                new ContentConfig(
                        3,
                        2,
                        2,
                        2
                )
        );
    }

    /**
     * Defines the configuration of the player's statistics and recovery effects.
     *
     * @param initialLucidity lucidity available at the beginning of the game
     * @param maxLucidity maximum lucidity allowed
     * @param initialComposure initial composure value
     * @param initialCaution initial caution value
     * @param minAttribute minimum value allowed for player attributes
     * @param maxAttribute maximum value allowed for player attributes
     * @param catchBreathRecovery lucidity restored by Catch Breath
     * @param memoryAttributeBonus bonus granted after discovering a memory
     * @param medicineHealing lucidity restored by medicine
     */
    public record PlayerConfig(
            int initialLucidity,
            int maxLucidity,
            int initialComposure,
            int initialCaution,
            int minAttribute,
            int maxAttribute,
            int catchBreathRecovery,
            int memoryAttributeBonus,
            int medicineHealing
    ) implements Serializable {}

    /**
     * Defines the attention limits, state thresholds and movement speeds
     * of the Presence.
     *
     * @param minAttention minimum attention value
     * @param maxAttention maximum attention value
     * @param dormantMaxAttention highest attention value for the dormant state
     * @param huntingMaxAttention highest attention value for the hunting state
     * @param dormantMovementSteps movement steps allowed while dormant
     * @param huntingMovementSteps movement steps allowed while hunting
     * @param unleashedMovementSteps movement steps allowed while unleashed
     */
    public record PresenceConfig(
            int minAttention,
            int maxAttention,
            int dormantMaxAttention,
            int huntingMaxAttention,
            int dormantMovementSteps,
            int huntingMovementSteps,
            int unleashedMovementSteps
    ) implements Serializable {}

    public record DiceConfig(int faces) implements Serializable{}

    /**
     * Defines the noise values produced by game actions and the attention
     * decrease applied during silent turns.
     *
     * @param movementNoise noise produced by movement
     * @param searchNoise noise produced by a noisy search
     * @param silentTurnAttentionDecrease attention removed after a silent turn
     */
    public record NoiseConfig(
            int movementNoise,
            int searchNoise,
            int silentTurnAttentionDecrease
    ) implements Serializable {}

    /**
     * Defines the configuration used by room search actions.
     * @param silentSearchThreshold minimum total required for a silent search
     */
    public record SearchConfig(int silentSearchThreshold) implements Serializable {}

    /**
     * Contains the balance settings used during confrontations.
     *
     * @param facePresenceThreshold minimum Composure check required to face the Presence
     * @param hideThreshold minimum Caution check required to hide
     * @param damage Lucidity lost after a failed confrontation action
     * @param retreatSteps maximum retreat distance after a successful Face Presence
     */
    public record ConfrontationConfig(
            int facePresenceThreshold,
            int hideThreshold,
            int damage,
            int retreatSteps
    ) implements Serializable {}

    /**
     * Defines the configuration of active decoys.
     *
     * @param holdTurns number of complete turns for which the Presence remains
     *                  in the decoy room after reaching it
     */
    public record DecoyConfig(int holdTurns) implements Serializable{}

    /**
     * Defines how many contents of each type are created for a game session.
     *
     * @param memoryCount number of memories
     * @param medicineCount number of medicines
     * @param decoyCount number of collectible decoys
     * @param emptyContentCount number of empty room contents
     */
    public record ContentConfig(
            int memoryCount,
            int medicineCount,
            int decoyCount,
            int emptyContentCount
    ) implements Serializable{}

    /**
     * Returns the number of turns required before the Listen action
     * becomes available again.
     *
     * @return the Listen cooldown duration in turns
     */
    public int listenCooldownTurns() {
        return LISTEN_COOLDOWN_TURNS;
    }

}
