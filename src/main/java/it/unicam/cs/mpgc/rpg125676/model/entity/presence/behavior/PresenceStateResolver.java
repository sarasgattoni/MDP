package it.unicam.cs.mpgc.rpg125676.model.entity.presence.behavior;

import it.unicam.cs.mpgc.rpg125676.model.entity.presence.PresenceState;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;

import java.util.Objects;

/**
 * Determines the behavioral state and movement capability of the Presence.
 *
 * The resolver converts an attention value into a corresponding
 * {@link PresenceState} and determines how many movement steps are
 * associated with that state.
 */
public class PresenceStateResolver {

    private GameSettings.PresenceConfig config;

    /**
     * Creates a resolver using the specified Presence configuration.
     *
     * @param config configuration containing attention values
     *               and movement values
     * @throws NullPointerException if config is null
     */
    public PresenceStateResolver(GameSettings.PresenceConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    /**
     * Determines the behavioral state associated with the given
     * attention value.
     *
     * The value is compared with the configured attention values
     * in order to classify the Presence as dormant, hunting or unleashed.
     *
     * @param attention current attention level of the Presence
     * @return the state corresponding to the supplied attention value
     * @throws IllegalArgumentException if attention is outside the
     *         configured valid range
     */
    public PresenceState resolve(int attention) {
        validateAttention(attention);

        if (attention <= config.dormantMaxAttention()) {return PresenceState.DORMANT;}

        if (attention <= config.huntingMaxAttention()) {return PresenceState.HUNTING;}
        return PresenceState.UNLEASHED;
    }

    /**
     * Returns the number of movement steps allowed for the specified state.
     *
     * The returned value is obtained from the current Presence configuration.
     *
     * @param state behavioral state of the Presence
     * @return number of rooms the Presence may cross during a turn
     * @throws NullPointerException if state is null
     */
    public int movementSteps(PresenceState state) {
        Objects.requireNonNull(state);

        return switch (state) {
            case DORMANT ->
                    config.dormantMovementSteps();

            case HUNTING ->
                    config.huntingMovementSteps();

            case UNLEASHED ->
                    config.unleashedMovementSteps();
        };
    }

    /**
     * Verifies that an attention value belongs to the configured range.
     *
     * @param attention attention value to validate
     * @throws IllegalArgumentException if the value is outside the allowed range
     */
    private void validateAttention(int attention) {
        if (attention < config.minAttention() || attention > config.maxAttention()) {
            throw new IllegalArgumentException("Attention is outside the allowed range");
        }
    }
}