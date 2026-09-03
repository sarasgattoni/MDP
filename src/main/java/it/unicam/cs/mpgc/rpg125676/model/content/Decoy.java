package it.unicam.cs.mpgc.rpg125676.model.content;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;

import java.util.Objects;

/**
 * Represents a decoy that can be discovered while searching a room.
 * When discovered, the decoy is added to the player's inventory
 * and can later be placed to produce noise and distract the Presence.
 */
public class Decoy extends AbstractRoomContent {

    /**
     * Creates a decoy with the specified discovery description.
     *
     * @param description description shown when the decoy is discovered
     * @throws IllegalArgumentException if description is null or blank
     */
    public Decoy(String description) {
        super(ContentType.DECOY, "Alarm Clock", description);
    }

    /**
     * Applies the effect of discovering the decoy.
     * The decoy is added to the player's inventory and the discovery
     * is immediately completed without requiring an additional decision.
     * @param player player discovering the decoy
     * @param settings current game configuration
     * @return the completed discovery result
     * @throws NullPointerException if player or settings is null
     */
    @Override
    public ContentDiscovery discover(Player player, GameSettings settings) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(settings);
        player.collectDecoy();
        return ContentDiscovery.completed(
                getDescription()
                        + " It has been added to your inventory."
        );
    }

}