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

}