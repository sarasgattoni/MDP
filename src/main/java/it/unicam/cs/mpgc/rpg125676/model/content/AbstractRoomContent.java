package it.unicam.cs.mpgc.rpg125676.model.content;

import java.util.Objects;

/**
 * Provides the common base implementation for room contents.
 * The class stores the information shared by every content type:
 * its {@link ContentType}, display name and description.
 */
public abstract class AbstractRoomContent implements RoomContent {

    private final ContentType type;
    private final String name;
    private final String description;

    /**
     * Creates a room content with the specified type, name and description.
     *
     * @param type type of the content
     * @param name display name of the content
     * @param description description shown when the content is discovered
     * @throws NullPointerException if type is null
     * @throws IllegalArgumentException if name or description is null or blank
     */
    protected AbstractRoomContent(ContentType type, String name, String description) {
        this.type = Objects.requireNonNull(type);

        if (name == null || name.isBlank()) {throw new IllegalArgumentException("Content name cannot be blank");}
        if (description == null || description.isBlank()) {throw new IllegalArgumentException("Content description cannot be blank");}
        this.name = name;
        this.description = description;
    }

    @Override
    public ContentType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
