package it.unicam.cs.mpgc.rpg125676.view.support;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Provides optional atmospheric messages used by the game view.
 */
public class AtmosphericEventProvider {

    private static final double EVENT_PROBABILITY = 0.45;

    private final RandomGenerator random;
    private final List<String> events;

    public AtmosphericEventProvider() {
        this(RandomGenerator.getDefault());
    }

    AtmosphericEventProvider(RandomGenerator random) {
        this.random = random;
        this.events =
                List.of(
                        "A floorboard creaks somewhere above you.",
                        "Something scratches behind the wall.",
                        "For a moment, the air smells of burned wood.",
                        "A door closes somewhere in the house.",
                        "The wallpaper seems to move in the corner of your eye.",
                        "A faint breath passes through the corridor.",
                        "You hear wood settling somewhere in the dark.",
                        "For one second, you are certain someone whispered your name."
                );
    }

    public Optional<String> nextEvent() {
        if (random.nextDouble() > EVENT_PROBABILITY) {
            return Optional.empty();
        }
        int index = random.nextInt(events.size());
        return Optional.of(events.get(index));
    }
}
