package it.unicam.cs.mpgc.rpg125676.model.entity.presence;

import it.unicam.cs.mpgc.rpg125676.model.entity.Entity;
import it.unicam.cs.mpgc.rpg125676.model.entity.behavior.Movable;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import java.util.ArrayDeque;
import java.util.Deque;

import java.util.Objects;

/**
 * Represents the hostile Presence that moves through the house
 * and reacts to the noise produced by the player.
 *
 * The class maintains the current room of the Presence, its attention
 * level and the history of its previous movements. The movement history
 * is used when the Presence is forced to retreat after a successful
 * confrontation.
 *
 * Attention values are always kept within the minimum and maximum
 * limits provided when the Presence is created.
 */
public class Presence implements Entity, Movable {

    private static final String NAME = "The Presence";

    private int minAttention;
    private int maxAttention;

    private Room currentRoom;
    private int attention;
    private final Deque<Room> movementHistory;

    /**
     * Creates a Presence in the specified starting room.
     *
     * The initial attention value is set to the configured minimum.
     *
     * @param startingRoom room in which the Presence starts the game
     * @param minAttention minimum allowed attention value
     * @param maxAttention maximum allowed attention value
     * @throws NullPointerException if startingRoom is null
     * @throws IllegalArgumentException if minAttention is negative
     *         or maxAttention is lower than minAttention
     */
    public Presence(Room startingRoom, int minAttention, int maxAttention) {
        if (minAttention < 0) {throw new IllegalArgumentException("Minimum attention cannot be negative");}
        if (maxAttention < minAttention) {throw new IllegalArgumentException("Maximum attention cannot be lower than minimum attention");}

        this.currentRoom = Objects.requireNonNull(startingRoom);
        this.minAttention = minAttention;
        this.maxAttention = maxAttention;
        this.attention = minAttention;
        this.movementHistory = new ArrayDeque<>();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Moves the Presence to the specified destination room.
     * @param destination room to move to
     * @throws NullPointerException if destination is null
     */
    @Override
    public void moveTo(Room destination) {
        Objects.requireNonNull(destination);
        if (currentRoom.equals(destination)) {
            return;
        }
        movementHistory.push(currentRoom);
        currentRoom = destination;
    }

    /**
     * Moves the Presence backwards through its recent
     * movement history.
     *
     * @param steps maximum number of rooms to retreat
     * @return actual number of rooms crossed
     */
    public int retreat(int steps) {
        if (steps < 0) {throw new IllegalArgumentException("Retreat steps cannot be negative");}

        int actualSteps = 0;

        while (actualSteps < steps && !movementHistory.isEmpty()) {
            currentRoom = movementHistory.pop();
            actualSteps++;
        }

        return actualSteps;
    }

    public int getAttention() {
        return attention;
    }

    /**
     * Increases the attention level by the specified amount.
     *
     * The resulting value cannot exceed the configured maximum attention.
     *
     * @param amount amount of attention to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void increaseAttention(int amount) {
        validatePositiveAmount(amount);

        attention = Math.min(maxAttention, attention + amount);
    }

    /**
     * Increases the attention level by the specified amount.
     *
     * The resulting value cannot exceed the configured maximum attention.
     *
     * @param amount amount of attention to add
     * @throws IllegalArgumentException if amount is negative
     */
    public void decreaseAttention(int amount) {
        validatePositiveAmount(amount);

        attention = Math.max(minAttention, attention - amount);
    }

    public void resetAttention() {
        attention = minAttention;
    }

    private void validatePositiveAmount(int amount) {
        if (amount < 0) {throw new IllegalArgumentException("Amount cannot be negative");}
    }
}
