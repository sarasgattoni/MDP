package it.unicam.cs.mpgc.rpg125676.model.game.factory;

import it.unicam.cs.mpgc.rpg125676.model.content.ContentType;
import it.unicam.cs.mpgc.rpg125676.model.game.GamePhase;
import it.unicam.cs.mpgc.rpg125676.model.game.GameSettings;
import it.unicam.cs.mpgc.rpg125676.model.game.GameState;
import it.unicam.cs.mpgc.rpg125676.model.game.GameStatus;
import it.unicam.cs.mpgc.rpg125676.model.game.engine.GameEngine;
import it.unicam.cs.mpgc.rpg125676.model.world.Room;
import it.unicam.cs.mpgc.rpg125676.model.world.RoomRole;
import it.unicam.cs.mpgc.rpg125676.model.world.loading.HouseLoader;
import it.unicam.cs.mpgc.rpg125676.persistence.world.JsonHouseLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class GameFactoryTest {

    private GameSettings settings;
    private GameEngine engine;
    private GameState state;

    @BeforeEach
    void setUp() {
        settings = GameSettings.standard();
        HouseLoader houseLoader = new JsonHouseLoader("/data/house.json");
        GameFactory factory = new GameFactory(settings, houseLoader);
        engine = factory.createNewGame("Test Player");
        state = engine.getState();
    }

    @Test
    void shouldCreateGameInProgress() {
        assertEquals(GameStatus.IN_PROGRESS, state.getStatus());
        assertFalse(state.isFinished());
    }

    @Test
    void shouldStartAtTurnZero() {
        assertEquals(0, state.getTurnCount());
    }

    @Test
    void playerShouldStartInStartRoom() {
        Room playerRoom = state.getPlayer().getCurrentRoom();

        assertEquals(RoomRole.START, playerRoom.getRole());
        assertEquals("entrance", playerRoom.getId());
    }

    @Test
    void presenceShouldStartInAllowedRoom() {
        Room presenceRoom = state.getPresence().getCurrentRoom();

        assertTrue(presenceRoom.isPresenceStartAllowed());
    }

    @Test
    void playerAndPresenceShouldStartInDifferentRooms() {
        assertNotEquals(state.getPlayer().getCurrentRoom(), state.getPresence().getCurrentRoom());
        assertEquals(GamePhase.EXPLORATION, state.getPhase());
    }

    @Test
    void houseShouldContainTwelveRooms() {
        assertEquals(12, state.getHouse().getRooms().size());
    }

    @Test
    void everySearchableRoomShouldReceiveContent() {
        state.getHouse().getRooms().stream().filter(Room::isSearchable).forEach(room -> assertTrue(room.getContent().isPresent(), "Missing content in room: " + room.getName()));
    }

    @Test
    void shouldContainExactlyNineSearchableRooms() {
        long count = state.getHouse().getRooms().stream().filter(Room::isSearchable).count();
        assertEquals(9, count);
    }

    @Test
    void shouldDistributeThreeMemories() {
        assertEquals(3, countContents(ContentType.MEMORY));
    }

    @Test
    void shouldDistributeTwoMedicines() {
        assertEquals(2, countContents(ContentType.MEDICINE));
    }

    @Test
    void shouldDistributeTwoDecoys() {
        assertEquals(2, countContents(ContentType.DECOY));
    }

    @Test
    void shouldDistributeTwoEmptyContents() {
        assertEquals(2, countContents(ContentType.EMPTY));
    }

    @Test
    void playerShouldStartWithConfiguredStatistics() {
        assertEquals(settings.player().initialLucidity(), state.getPlayer().getStats().getLucidity());
        assertEquals(settings.player().initialComposure(), state.getPlayer().getStats().getComposure());
        assertEquals(settings.player().initialCaution(), state.getPlayer().getStats().getCaution());
    }

    @Test
    void presenceShouldStartWithMinimumAttention() {
        assertEquals(settings.presence().minAttention(), state.getPresence().getAttention());
    }

    private long countContents(ContentType type) {
        return state.getHouse().getRooms().stream().filter(Room::isSearchable).map(Room::getContent).map(optional -> optional.orElseThrow()).filter(content -> content.getType() == type).count();
    }
}