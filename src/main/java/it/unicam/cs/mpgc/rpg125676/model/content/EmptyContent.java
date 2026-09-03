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


}