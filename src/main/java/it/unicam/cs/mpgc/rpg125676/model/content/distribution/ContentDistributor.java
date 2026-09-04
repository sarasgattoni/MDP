package it.unicam.cs.mpgc.rpg125676.model.content.distribution;

import it.unicam.cs.mpgc.rpg125676.model.content.RoomContent;
import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Distributes room contents randomly among all searchable rooms
 * of a house.
 * The distributor verifies that the number of available contents
 * matches the number of searchable rooms before performing the
 * assignment.
 */
public class ContentDistributor {

    private final Random random;

    /**
     * Creates a content distributor using the specified random generator.
     *
     * @param random random generator used to shuffle room contents
     * @throws NullPointerException if random is null
     */
    public ContentDistributor(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    /**
     * Randomly assigns the supplied contents to the searchable rooms
     * of the given house.
     * Each searchable room receives exactly one content. Before the
     * assignment, the contents are shuffled using the configured random
     * generator.
     *
     * @param house house containing the searchable rooms
     * @param contents contents to distribute
     * @throws NullPointerException if house or contents is null
     * @throws IllegalArgumentException if the number of contents does not
     *         match the number of searchable rooms
     */
    public void distribute(House house, List<? extends RoomContent> contents) {
        Objects.requireNonNull(house);
        Objects.requireNonNull(contents);

        List<Room> searchableRooms = house.getRooms()
                        .stream()
                        .filter(Room::isSearchable)
                        .toList();

        validateContentCount(searchableRooms, contents);

        List<RoomContent> shuffledContents = new ArrayList<>(contents);

        Collections.shuffle(shuffledContents, random);

        for (int i = 0; i < searchableRooms.size(); i++) {
            searchableRooms
                    .get(i)
                    .setContent(shuffledContents.get(i));
        }
    }

    /**
     * Verifies that each searchable room can receive exactly one content.
     *
     * @param searchableRooms searchable rooms of the house
     * @param contents contents available for distribution
     * @throws IllegalArgumentException if the two collections have
     *         different sizes
     */
    private void validateContentCount(List<Room> searchableRooms, List<? extends RoomContent> contents) {
        if (searchableRooms.size() != contents.size()) {
            throw new IllegalArgumentException("The number of contents must match " + "the number of searchable rooms");
        }
    }
}
