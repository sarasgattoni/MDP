package it.unicam.cs.mpgc.rpg125676.persistence.world;

import it.unicam.cs.mpgc.rpg125676.model.world.House;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import it.unicam.cs.mpgc.rpg125676.model.world.loading.HouseLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonHouseLoaderTest {

    private House house;

    @BeforeEach
    void setUp() {
        HouseLoader loader = new JsonHouseLoader("/data/house.json");
        house = loader.load();
    }

    @Test
    void shouldLoadTwelveRooms() {
        assertEquals(12, house.getRooms().size());
    }

    @Test
    void shouldLoadNineSearchableRooms() {
        long searchableRooms = house.getRooms().stream().filter(Room::isSearchable).count();

        assertEquals(9, searchableRooms);
    }

    @Test
    void entranceShouldBeStartRoom() {
        Room entrance = house.getRoom("entrance");

        assertEquals(RoomRole.START, entrance.getRole());
        assertFalse(entrance.isSearchable());
    }

    @Test
    void lockedRoomShouldBeFinalRoom() {
        Room lockedRoom = house.getRoom("locked_room");

        assertEquals(RoomRole.FINAL, lockedRoom.getRole());
        assertFalse(lockedRoom.isSearchable());
    }

    @Test
    void entranceShouldConnectToHallwayAndLivingRoom() {
        Room entrance = house.getRoom("entrance");
        Room hallway = house.getRoom("hallway");
        Room livingRoom = house.getRoom("living_room");

        assertTrue(house.areConnected(entrance, hallway));
        assertTrue(house.areConnected(entrance, livingRoom));
    }

    @Test
    void lockedRoomShouldConnectOnlyToParentsBedroom() {
        Room lockedRoom = house.getRoom("locked_room");
        Room parentsBedroom = house.getRoom("parents_bedroom");

        assertEquals(1, house.getAdjacentRooms(lockedRoom).size());
        assertTrue(house.areConnected(lockedRoom, parentsBedroom));
    }
}