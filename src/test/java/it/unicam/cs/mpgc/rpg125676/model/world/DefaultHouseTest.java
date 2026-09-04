package it.unicam.cs.mpgc.rpg125676.model.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultHouseTest {

    private DefaultHouse house;
    private Room entrance;
    private Room hallway;
    private Room kitchen;

    @BeforeEach
    void setUp() {
        house = new DefaultHouse();

        entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);

        hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);

        kitchen = new Room("kitchen", "Kitchen", true, RoomRole.STANDARD, false);

        house.addRoom(entrance);
        house.addRoom(hallway);
        house.addRoom(kitchen);

        house.connectRooms("entrance", "hallway");

        house.connectRooms("hallway", "kitchen");
    }

    @Test
    void connectionShouldBeBidirectional() {
        assertTrue(house.areConnected(entrance, hallway));

        assertTrue(house.areConnected(hallway, entrance));
    }

    @Test
    void unconnectedRoomsShouldNotBeAdjacent() {
        assertFalse(house.areConnected(entrance, kitchen));
    }

    @Test
    void shouldReturnRoomById() {
        assertSame(kitchen, house.getRoom("kitchen"));
    }
}