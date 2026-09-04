package it.unicam.cs.mpgc.rpg125676.model.game.dice;

import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Rolls the dice using a pseudo-random number generator.
 * The roller generates values between 1 and the configured number
 * of faces and stores the result of the most recent roll.
 */
public class DiceRoller {

    private final int faces;
    private final RandomGenerator random;
    private int lastRoll;

    /**
     * Creates a dice roller using the supplied game configuration.
     * The number of faces is obtained from the configuration and the
     * default random generator provided by the Java runtime is used.
     *
     * @param config configuration containing the number of die faces
     * @throws NullPointerException if config is null
     * @throws IllegalArgumentException if the configured die has fewer
     *         than two faces
     */
    public DiceRoller(GameSettings.DiceConfig config) {
        Objects.requireNonNull(config);

        this.faces = config.faces();
        this.random = RandomGenerator.getDefault();
    }

    public int roll() {
        lastRoll = random.nextInt(1, faces + 1);
        return lastRoll;
    }
    public int getLastRoll() {
        if (lastRoll == 0) {throw new IllegalStateException("The dice has not been rolled yet");
        }
        return lastRoll;
    }
}
