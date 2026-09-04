package it.unicam.cs.mpgc.rpg125676.model.movement;

import it.unicam.cs.mpgc.rpg125676.model.world.DefaultHouse;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BfsPathFinderTest {

    private DefaultHouse house;
    private PathFinder pathFinder;

    private Room entrance;
    private Room hallway;
    private Room kitchen;
    private Room pantry;

    @BeforeEach
    void setUp() {
        house = new DefaultHouse();
        pathFinder = new BfsPathFinder();

        entrance = new Room("entrance", "Entrance", false, RoomRole.STANDARD, false);

        hallway = new Room("hallway", "Hallway", true, RoomRole.STANDARD, false);

        kitchen = new Room("kitchen", "Kitchen", true, RoomRole.STANDARD, false);

        pantry = new Room("pantry", "Pantry", true, RoomRole.STANDARD, false);

        house.addRoom(entrance);
        house.addRoom(hallway);
        house.addRoom(kitchen);
        house.addRoom(pantry);

        house.connectRooms("entrance", "hallway");
        house.connectRooms("hallway", "kitchen");
        house.connectRooms("kitchen", "pantry");
    }

    @Test
    void shouldFindShortestPath() {
        List<Room> path = pathFinder.findPath(house, entrance, pantry);

        assertEquals(List.of(entrance, hallway, kitchen, pantry), path);
    }

    @Test
    void pathFromRoomToItselfShouldContainOnlyThatRoom() {
        List<Room> path = pathFinder.findPath(house, kitchen, kitchen);

        assertEquals(List.of(kitchen), path);
    }
}