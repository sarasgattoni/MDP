package it.unicam.cs.mpgc.rpg125676.model.content;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.decision.MemoryAttributeDecision;

import java.util.Objects;

/**
 * Represents one of the memories required to uncover the identity
 * of the Presence and progress toward the final objective.
 * Each memory has a sequence number that identifies its position
 * within the narrative. When discovered, it is registered by the
 * player and creates a decision that allows one player attribute
 * to be increased.
 */
public class Memory extends AbstractRoomContent {

    private final int sequenceNumber;

    /**
     * Creates a memory with the specified sequence number and description.
     *
     * @param sequenceNumber positive number identifying the memory
     *                       within the narrative sequence
     * @param description description revealed when the memory is discovered
     * @throws IllegalArgumentException if sequenceNumber is not positive
     *         or description is null or blank
     */
    public Memory(int sequenceNumber, String description) {
        super(ContentType.MEMORY, "Memory", description);

        if (sequenceNumber <= 0) {throw new IllegalArgumentException("Memory sequence number must be positive");}
        this.sequenceNumber = sequenceNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Applies the effect of discovering this memory.
     * The memory is registered by the player and a new
     * {@link MemoryAttributeDecision} is created. The bonus associated
     * with the decision is obtained from the current game configuration.
     * @param player player discovering the memory
     * @param settings current game configuration
     * @return a discovery result containing the required attribute decision
     * @throws NullPointerException if player or settings is null
     */
    @Override
    public ContentDiscovery discover(Player player, GameSettings settings) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(settings);
        player.registerMemoryFound();
        int bonus =
                settings
                        .player()
                        .memoryAttributeBonus();

        MemoryAttributeDecision decision = new MemoryAttributeDecision(bonus);

        return ContentDiscovery.requiringDecision(getDescription(), decision);
    }

}