package it.unicam.cs.mpgc.rpg125676.model.content;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;

import java.util.Objects;

/**
 * Represents the absence of useful content inside a searchable room.
 * This class acts as a Null Object implementation of {@link RoomContent},
 * allowing an empty room to be handled like any other room content
 * without using null values or special checks.
 * Discovering this content produces no change to the player's state.
 */
public class EmptyContent extends AbstractRoomContent {

    /**
     * Creates empty room content with the specified description.
     *
     * @param description description shown when the empty content is discovered
     * @throws IllegalArgumentException if description is null or blank
     */
    public EmptyContent(String description) {
        super(ContentType.EMPTY, "Nothing", description);
    }

    /**
     * Resolves the discovery of an empty room content.
     * No gameplay effect is applied to the player and the discovery
     * is immediately completed.
     * @param player player discovering the content
     * @param settings current game configuration
     * @return the completed discovery result
     * @throws NullPointerException if player or settings is null
     */
    @Override
    public ContentDiscovery discover(Player player, GameSettings settings) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(settings);

        return ContentDiscovery.completed(getDescription());
    }

}