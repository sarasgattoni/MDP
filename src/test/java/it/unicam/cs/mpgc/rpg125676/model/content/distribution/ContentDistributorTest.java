package it.unicam.cs.mpgc.rpg125676.model.content.distribution;

import it.unicam.cs.mpgc.rpg125676.model.content.ContentType;
import it.unicam.cs.mpgc.rpg125676.model.content.EmptyContent;
import it.unicam.cs.mpgc.rpg125676.model.content.Memory;
import it.unicam.cs.mpgc.rpg125676.model.content.RoomContent;
import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ContentDistributorTest {

    private DefaultHouse house;

    private Room firstRoom;
    private Room secondRoom;
    private Room nonSearchableRoom;

    @BeforeEach
    void setUp() {
        house = new DefaultHouse();

        firstRoom = new Room("first", "First Room", true, RoomRole.STANDARD, false);

        secondRoom = new Room("second", "Second Room", true, RoomRole.STANDARD, false);

        nonSearchableRoom = new Room("third", "Third Room", false, RoomRole.STANDARD, false);

        house.addRoom(firstRoom);
        house.addRoom(secondRoom);
        house.addRoom(nonSearchableRoom);
    }

    @Test
    void shouldAssignContentToEverySearchableRoom() {
        ContentDistributor distributor = new ContentDistributor(new Random(42));

        List<RoomContent> contents = List.of(new Memory(1, "Test memory"), new EmptyContent("Nothing was found"));

        distributor.distribute(house, contents);

        assertTrue(firstRoom.getContent().isPresent());

        assertTrue(secondRoom.getContent().isPresent());
    }

    @Test
    void shouldNotAssignContentToNonSearchableRooms() {
        ContentDistributor distributor = new ContentDistributor(new Random(42));

        List<RoomContent> contents = List.of(new Memory(1, "Test memory"), new EmptyContent("Nothing was found"));

        distributor.distribute(house, contents);

        assertTrue(nonSearchableRoom.getContent().isEmpty());
    }

    @Test
    void distributionShouldPreserveAllContents() {
        ContentDistributor distributor = new ContentDistributor(new Random(42));

        List<RoomContent> contents = List.of(new Memory(1, "Test memory"), new EmptyContent("Nothing was found"));

        distributor.distribute(house, contents);

        Set<ContentType> distributedTypes =
                house.getRooms()
                        .stream()
                        .filter(Room::isSearchable)
                        .map(Room::getContent)
                        .map(optional -> optional.orElseThrow())
                        .map(RoomContent::getType)
                        .collect(Collectors.toSet());

        assertEquals(Set.of(ContentType.MEMORY, ContentType.EMPTY), distributedTypes);
    }

    @Test
    void shouldRejectWrongNumberOfContents() {
        ContentDistributor distributor = new ContentDistributor(new Random(42));

        List<RoomContent> contents = List.of(new EmptyContent("Nothing"));

        assertThrows(IllegalArgumentException.class, () -> distributor.distribute(house, contents));
    }
}