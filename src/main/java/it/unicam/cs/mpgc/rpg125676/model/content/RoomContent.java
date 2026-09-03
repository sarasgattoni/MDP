package it.unicam.cs.mpgc.rpg125676.model.content;

import it.unicam.cs.mpgc.rpg125676.model.entity.player.Player;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;

import java.io.Serializable;

/**
 * Defines the common contract for content that can be discovered
 * while searching a room.
 * Each content provides a type, a name and a description, and defines
 * the effect produced when it is discovered by the player.
 */
public interface RoomContent extends Serializable {

    ContentType getType();

    String getName();

    String getDescription();

    /**
     * Applies the effect produced when this content is discovered.
     *
     * @param player player discovering the content
     * @param settings game configuration
     * @return discovery result
     */
    ContentDiscovery discover(Player player, GameSettings settings);
}